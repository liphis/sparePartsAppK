package de.abas.esdk.spart.utils;

import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.db.FieldValueProvider;
import de.abas.jfop.base.buffer.BufferFactory;
import de.abas.jfop.base.buffer.ScreenBuffer;

import java.util.List;

public class ScreenProtection {

    private List<String> headerFieldset;
    private List<String> tableFieldset;
    BufferFactory bufferFactory = null;
    ScreenBuffer screenBuffer = null;


    public ScreenProtection(List<String> headerFieldset){
        this.headerFieldset = headerFieldset;
        bufferFactory = BufferFactory.newInstance(true);
        screenBuffer = bufferFactory.getScreenBuffer();
    }

    public  ScreenProtection(List<String> headerFieldset, List<String> tableFieldSet){
        this.headerFieldset = headerFieldset;
        this.tableFieldset = tableFieldSet;
        bufferFactory = BufferFactory.newInstance(true);
        screenBuffer = bufferFactory.getScreenBuffer();
    }

    public void protectHeaderFields(){
        for(String field : headerFieldset){
            screenBuffer.setWriteProtection(true, field);
        }
    }

    public void protectTableFields(){
        for(String column : tableFieldset){
            screenBuffer.setColumnWriteProtection(true, column);
        }
    }
}
