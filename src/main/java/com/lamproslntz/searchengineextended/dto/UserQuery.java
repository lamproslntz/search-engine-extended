package com.lamproslntz.searchengineextended.dto;

/**
 * @author Lampros Lountzis
 */
public class UserQuery {

    private String query;

    public UserQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserQuery userQuery = (UserQuery) o;

        return getQuery().equals(userQuery.getQuery());
    }

    @Override
    public int hashCode() {
        return getQuery().hashCode();
    }

    @Override
    public String toString() {
        return "UserQuery { " +
                "query='" + query + '\'' +
                " }";
    }

}
