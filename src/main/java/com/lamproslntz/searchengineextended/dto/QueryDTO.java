package com.lamproslntz.searchengineextended.dto;

/**
 * @author Lampros Lountzis
 */
public class QueryDTO {

    private String query;

    public QueryDTO() {
    }

    public QueryDTO(String query) {
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

        QueryDTO queryDTO = (QueryDTO) o;

        return getQuery().equals(queryDTO.getQuery());
    }

    @Override
    public int hashCode() {
        return getQuery().hashCode();
    }

    @Override
    public String toString() {
        return "QueryDTO { " +
                "query='" + query + '\'' +
                " }";
    }

}
