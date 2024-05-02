package com.example.videosharingapi.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {
    private static MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String decode(String code, String... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
