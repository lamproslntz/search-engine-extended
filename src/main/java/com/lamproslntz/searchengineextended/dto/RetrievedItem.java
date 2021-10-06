package com.lamproslntz.searchengineextended.dto;

import org.apache.lucene.document.Document;

/**
 * @author Lampros Lountzis
 */
public class RetrievedItem {

    private String id;
    private String title;
    private String author;
    private String content;

    private String score;

    public RetrievedItem(String id, String title, String author, String content, String score) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.score = score;
    }

    public RetrievedItem(Document document, float score) {
        this.id = document.get("id");
        this.title = document.get("title");
        this.author = document.get("author");
        this.content = document.get("abstract");
        this.score = String.valueOf(score);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetrievedItem that = (RetrievedItem) o;

        if (!getId().equals(that.getId())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        if (!getAuthor().equals(that.getAuthor())) return false;
        if (!getContent().equals(that.getContent())) return false;
        return getScore().equals(that.getScore());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getAuthor().hashCode();
        result = 31 * result + getContent().hashCode();
        result = 31 * result + getScore().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserResult { " +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", score='" + score + '\'' +
                " }";
    }

}
