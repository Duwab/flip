package com.dooweb.flip.objects;


public interface CallbackInterface{
    void complete(DataObject res, HTTPStatus err);
    void progress(double percent);
    void start();
}