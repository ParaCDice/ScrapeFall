package csc.paradise.piper.scrapefall.models;

import net.dzultra.jsa.cards.enums.OrderMode;
import net.dzultra.jsa.cards.enums.SortMode;

import java.io.Serializable;

public class User implements Serializable {

    private OrderMode lastUsedOrderMode;
    private SortMode lastUsedSortMode;

    public User(OrderMode lastUsedOrderMode, SortMode lastUsedSortMode) {
        setLastUsedOrderMode(lastUsedOrderMode);
        setLastUsedSortMode(lastUsedSortMode);
    }

    public OrderMode getLastUsedOrderMode() {
        return lastUsedOrderMode;
    }

    public void setLastUsedOrderMode(OrderMode lastUsedOrderMode) {
        this.lastUsedOrderMode = lastUsedOrderMode;
    }

    public SortMode getLastUsedSortMode() {
        return lastUsedSortMode;
    }

    public void setLastUsedSortMode(SortMode lastUsedSortMode) {
        this.lastUsedSortMode = lastUsedSortMode;
    }
}
