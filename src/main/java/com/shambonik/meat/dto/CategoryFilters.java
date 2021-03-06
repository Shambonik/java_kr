package com.shambonik.meat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.util.*;

@Data
public class CategoryFilters{
    @Data
    @NoArgsConstructor
    public static class Pair{
        private String name;
        private boolean checked;

        public Pair(String name) {
            this.name = name;
        }
    }
    private List<Pair> list = new ArrayList<Pair>();

    public Pair add(String name){
        Pair pair = new Pair(name);
        list.add(pair);
        return pair;
    }
}
