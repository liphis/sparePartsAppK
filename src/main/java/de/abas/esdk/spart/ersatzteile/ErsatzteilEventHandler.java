package de.abas.esdk.spart.ersatzteile;

import de.abas.eks.jfop.remote.FO;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ScreenEvent;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.common.type.enums.EnumEditorAction;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.custom.ersatzteileapp.Ersatzteil;
import de.abas.erp.db.schema.custom.ersatzteileapp.ErsatzteilEditor;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;
import de.abas.esdk.spart.utils.ScreenProtection;
import de.abas.esdk.spart.utils.SystemInformation;

import java.util.ArrayList;
import java.util.List;


@EventHandler(head = ErsatzteilEditor.class, row = ErsatzteilEditor.Row.class)

@RunFopWith(EventHandlerRunner.class)

public class ErsatzteilEventHandler {

    private final SystemInformation systemInformation = new SystemInformation();

    @ScreenEventHandler(type = ScreenEventType.ENTER)
    public void screenEnter(ScreenEvent event, ScreenControl screenControl, DbContext ctx, ErsatzteilEditor head)
            throws EventException {
        if (event.getCommand().equals(EnumEditorAction.New)) {
            if (!systemInformation.isEdpMode()) {
                FO.box("Fehler", "Anlage nicht möglich!");
                throw new EventException(1);
            }
        }

        if (event.getCommand().equals(EnumEditorAction.Edit)) {
            List<String> headerProtectedFields = getHeaderFieldset();
            List<String> tableProtectedFields = getTableFieldset();

            ScreenProtection screenProtection = new ScreenProtection(headerProtectedFields, tableProtectedFields);
            screenProtection.protectHeaderFields();
            screenProtection.protectTableFields();
        }
    }

    private List <String> getHeaderFieldset(){
        List<String> headerProtectedFields = new ArrayList<>();
        headerProtectedFields.add(Ersatzteil.META.idno.getName());
        headerProtectedFields.add(Ersatzteil.META.swd.getName());
        headerProtectedFields.add(Ersatzteil.META.descr.getName());
        headerProtectedFields.add(Ersatzteil.META.yspartartikel.getName());
        return headerProtectedFields;
    }

    private List<String> getTableFieldset() {
        List<String> tableProtectedFields = new ArrayList<>();
        tableProtectedFields.add(Ersatzteil.Row.META.yspartverwdatum.getName());
        tableProtectedFields.add(Ersatzteil.Row.META.yspartverwgrund.getName());
        return tableProtectedFields;
    }
}
