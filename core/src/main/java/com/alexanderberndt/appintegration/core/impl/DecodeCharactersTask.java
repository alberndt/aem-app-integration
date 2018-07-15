package com.alexanderberndt.appintegration.core.impl;

import com.alexanderberndt.appintegration.api.AbstractIntegrationTask;
import com.alexanderberndt.appintegration.api.IntegrationContext;

import java.io.UnsupportedEncodingException;

public class DecodeCharactersTask extends AbstractIntegrationTask<byte[], String> {

    public DecodeCharactersTask() {
        super(byte[].class, String.class);
    }

    @Override
    public String execute(byte[] data, IntegrationContext context) {
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            context.addError("Cannot convert ", e.getMessage(), e);
            return null;
        }
    }
}
