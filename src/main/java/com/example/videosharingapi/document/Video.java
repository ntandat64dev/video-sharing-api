package com.example.videosharingapi.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = Indexes.VIDEO)
@Getter
@Setter
public class Video {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Date, index = false,
            format = { DateFormat.date_hour_minute_second, DateFormat.date_hour_minute })
    private LocalDateTime publishedDate;

    @Field(type = FieldType.Long, index = false)
    private Long viewCount;
}
