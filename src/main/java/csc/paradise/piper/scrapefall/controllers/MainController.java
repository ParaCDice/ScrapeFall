package csc.paradise.piper.scrapefall.controllers;

import com.google.gson.Gson;
import csc.paradise.piper.scrapefall.models.LoadedView;
import csc.paradise.piper.scrapefall.NodeUtils;
import csc.paradise.piper.scrapefall.services.UserDataAccessor;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.TilePane;
import net.dzultra.jsa.ScryfallClient;
import net.dzultra.jsa.cards.Card;
import net.dzultra.jsa.cards.CardList;
import net.dzultra.jsa.cards.CardRequester;
import net.dzultra.jsa.cards.CardSearchExecutor;
import net.dzultra.jsa.cards.enums.OrderMode;
import net.dzultra.jsa.cards.enums.SortMode;
import net.dzultra.jsa.exceptions.CardSearchException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainController {
    @FXML
    private TilePane cardPaneContainer;
    @FXML
    private ScrollPane cardScrollPane;
    @FXML
    private Button scrapeButton;
    @FXML
    private Button getCardsButton;
    @FXML
    private TilePane pricePaneContainer;
    @FXML
    private Label totalPriceLabelBackDrop;
    @FXML
    private Label totalPriceLabel;
    private int currentPricePaneContCount = 0;
    private int lastPricePaneContCount = 0;
    @FXML
    private Label cardsErrorLabel;
    @FXML
    private Label scrapeErrorLabel;
    @FXML
    private TextField queryField;
    @FXML
    private Button lastPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private ChoiceBox sortModeChoiceBox;
    @FXML
    private ChoiceBox sortDirChoiceBox;

    static CardRequester cardRequester;

    private String query = "";

    private final Pattern pricePattern = Pattern.compile("\\$([0-9]+.[0-9]{2})");

    private final ScryfallClient scrapefallClient = new ScryfallClient("Scrape-Fall");
    private final Gson scryfallGson = new Gson();

    private final List<CardList> cardListPages = new ArrayList<>();
    private int currentPageCount = 0;
    private int lastPageCount = 0;
    private CardList currentCardList;

    private UserDataAccessor userDataAccessor;

    @FXML
    protected void initialize() {
        userDataAccessor = UserDataAccessor.getInstance();
        cardRequester = new CardRequester(new ScryfallClient("Scrape-Fall"));

        sortModeChoiceBox.setItems(FXCollections.observableArrayList(OrderMode.values()));
        sortModeChoiceBox.getSelectionModel().select(OrderMode.NAME);
        sortDirChoiceBox.setItems(FXCollections.observableArrayList(SortMode.values()));
        sortDirChoiceBox.getSelectionModel().select(SortMode.AUTO);

        if(userDataAccessor.getCurrentUser()!=null) {
            sortModeChoiceBox.getSelectionModel().select(userDataAccessor.getCurrentUser().getLastUsedOrderMode());
            sortDirChoiceBox.getSelectionModel().select(userDataAccessor.getCurrentUser().getLastUsedSortMode());
        }else{
            userDataAccessor.updateUser((OrderMode) sortModeChoiceBox.getSelectionModel().getSelectedItem(),(SortMode) sortDirChoiceBox.getSelectionModel().getSelectedItem());
        }

        cardScrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
            checkVisibleNodes();
        });

        frameLoop();
    }

    @FXML
    protected void getCards() {
        cardsErrorLabel.setText("");
        if (query.isEmpty()) {
            cardsErrorLabel.setText("Please enter a query");
            return;
        }
        try {
            if (currentCardList == null || lastPageCount==currentPageCount) {
                currentCardList = cardRequester.getCardsByQuery(query, null,
                        (OrderMode) sortModeChoiceBox.getSelectionModel().getSelectedItem(),
                        (SortMode) sortDirChoiceBox.getSelectionModel().getSelectedItem());
            }
            userDataAccessor.updateUser((OrderMode) sortModeChoiceBox.getSelectionModel().getSelectedItem(),(SortMode) sortDirChoiceBox.getSelectionModel().getSelectedItem());
        } catch (CardSearchException cardSearchException) {
            cardsErrorLabel.setText("No cards matching query");
            return;
        }

        cardPaneContainer.getChildren().clear();

        for (Card card : currentCardList.cards()) {
            if (card.card_faces() != null && card.card_faces().getFirst().image_uris()!=null) {
                LoadedView<DoubleCardPaneController> loadedView;
                loadedView = NodeUtils.loadFXML("double-card-pane.fxml");
                DoubleCardPaneController cpc = loadedView.controller();
                try {
                    cpc.setCard(card);
                    loadedView.root().setUserData(loadedView.controller());
                    cardPaneContainer.getChildren().add(loadedView.root());
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    System.err.println("WARNING: FXML for " + card.name() + " did not load.");
                }
            } else {
                LoadedView<CardPaneController> loadedView;
                loadedView = NodeUtils.loadFXML("card-pane.fxml");
                CardPaneController cpc = loadedView.controller();
                try {
                    cpc.setCard(card);
                    loadedView.root().setUserData(loadedView.controller());
                    cardPaneContainer.getChildren().add(loadedView.root());
                }catch (NullPointerException npe){
                    npe.printStackTrace();
                    System.err.println("WARNING: FXML for " + card.name() + " did not load.");
                }
            }
        }
    }


    public void frameLoop() {
        new AnimationTimer() {
            long lastUpdate = -1;
            double deltaTime;


            private void update() {
                currentPricePaneContCount = pricePaneContainer.getChildren().toArray().length;

                if (lastPricePaneContCount != currentPricePaneContCount) {
                    float totalPrice = getTotalPriceOfScrapedCardsInContainer().floatValue();
                    String totalPriceString = NumberFormat.getCurrencyInstance().format(totalPrice);
                    totalPriceLabelBackDrop.setText(totalPriceString);
                    totalPriceLabel.setText(totalPriceString);
                }

                lastPricePaneContCount = currentPricePaneContCount;
            }

            @Override
            public void handle(long now) {
                if (lastUpdate < 0) {
                    lastUpdate = now;
                    return;
                }

                deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                update();
                lastUpdate = now;
            }
        }.start();
    }


    private BigDecimal getTotalPriceOfScrapedCardsInContainer() {
        ArrayList<Node> descendents = NodeUtils.getAllNodes(pricePaneContainer);
        BigDecimal totalPrice = new BigDecimal(0);

        for (Node node : descendents) {
            try {
                if (node.getId().equalsIgnoreCase("pricePane")) {
                    try {
                        BigDecimal price = ((ScrapedCardController) node.getUserData()).getPrice();
                        totalPrice = totalPrice.add(price);
                    } catch (Exception e) {
                        System.err.println("WARNING: Failed on node id=" + node.getId() + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }
        return totalPrice;
    }


    @FXML
    protected void onExit() {
        System.out.println("hi");
        System.exit(0);
    }

    public void onQueryFieldTextChanged(KeyEvent inputMethodEvent) {
        query = queryField.getText();
    }

    public void checkVisibleNodes() {
        for (Node node : cardPaneContainer.getChildren()) {
            if (NodeUtils.isNodeVisible(node, cardScrollPane) && node.getUserData() instanceof CardPaneController visibleCard) {
                visibleCard.loadImage();
            }
        }

    }

    public void onNextPage() {
        if (currentCardList != null) {
            if (currentCardList.has_more()) {
                System.out.println("trying ?");
                try {
                    lastPageCount = currentPageCount;
                    CardList retrievedCL = CardSearchExecutor.executeCardSearch(scrapefallClient, scryfallGson, currentCardList.next_page());
                    System.out.println(retrievedCL);
                    this.currentPageCount++;
                    cardListPages.add(currentCardList);
                    currentCardList = retrievedCL;
                    cardScrollPane.setVvalue(0);
                    getCards();
                    System.out.println("should be done?");
                } catch (CardSearchException cardSearchException) {
                    System.err.println("WARNING: Current Card List's next page wasn't retrieved.");
                    cardsErrorLabel.setText("Next Page (maybe) Isn't Real");
                }
            }
        }
    }

    public void onPrevPage() {
        if (currentCardList != null) {
            if (currentPageCount>0) {
                lastPageCount = currentPageCount;
                currentPageCount--;
                currentCardList = cardListPages.get(currentPageCount);
                cardScrollPane.setVvalue(0);
                getCards();
            }
        }
    }

    public void onKeyPressed(KeyEvent keyEvent) {

        if (keyEvent.getCode() == KeyCode.ENTER) {
            getCards();
        }

    }
}
