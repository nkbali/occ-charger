package com.mtn.madapi.payments.occ.occcharger.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("main")
public class MainController {

    @GetMapping
    public ResponseEntity<?> chargeOCC(){

        try{

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return ResponseEntity.ok().build();
    }

}
