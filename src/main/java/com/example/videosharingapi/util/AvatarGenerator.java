package com.example.videosharingapi.util;

public class AvatarGenerator {

    public static String getUrl(String name, int size) {
        return "https://ui-avatars.com/api/?name=%s&size=%s&background=random&color=fff&rouded=true&bold=true"
                .formatted(name, size);
    }
}
