package de.abas.esdk.spart.utils;

import de.abas.jfop.base.buffer.BufferFactory;
import de.abas.jfop.base.buffer.GlobalTextBuffer;

public class SystemInformation {

    private GlobalTextBuffer globalTextBuffer;

    public SystemInformation(){
        globalTextBuffer = BufferFactory.newInstance(true).getGlobalTextBuffer();
    }

    public boolean isEdpMode(){
        return  globalTextBuffer.getBooleanValue("runByEDP");
    }
}
