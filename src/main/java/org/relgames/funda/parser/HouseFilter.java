package org.relgames.funda.parser;

import java.util.ArrayList;
import java.util.List;

/**
* @author opoleshuk
*/
public enum HouseFilter {
    VE(new Filter() {
        @Override
        public boolean apply(House house) {
            return house.getErfpacht().toLowerCase().contains("volle eigendom");
        }
    }),

    NE(new Filter() {
        @Override
        public boolean apply(House house) {
            return !house.getErfpacht().toLowerCase().contains("erfpacht");
        }
    });

    private final Filter houseFilter;

    private HouseFilter(Filter houseFilter) {
        this.houseFilter = houseFilter;
    }

    public List<House> filter(List<House> houses) {
        List<House> result = new ArrayList<>();
        for (House house : houses) {
            if (houseFilter.apply(house)) {
                result.add(house);
            }
        }
        return result;
    }

    private interface Filter {
        boolean apply(House house);
    }
}
