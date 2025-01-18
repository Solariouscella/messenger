/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.kcgi.web.messenger.model;

/**
 *
 * @author Administrator
 */
public class TranslationResponse {
    private long id;
    private String author;
    private String originalMessage;
    private String translatedMessage;
    
     // Default constructor (required for frameworks like Jersey or Jackson)
    public TranslationResponse() {
    }
    
    // Constructor
    public TranslationResponse(long id, String author, String originalMessage, String translatedMessage) {
        this.id = id;
        this.author = author;
        this.originalMessage = originalMessage;
        this.translatedMessage = translatedMessage;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getTranslatedMessage() {
        return translatedMessage;
    }

    public void setTranslatedMessage(String translatedMessage) {
        this.translatedMessage = translatedMessage;
    }
}