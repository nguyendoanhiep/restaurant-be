package com.tom.mycat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "url_image")
    private String url_image;
    @Column(name = "create_date")
    private Date createDate;
    @Column(name = "modified_date")
    private Date modifiedDate;

}