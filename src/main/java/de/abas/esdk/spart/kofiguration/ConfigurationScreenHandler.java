package de.abas.esdk.spart.kofiguration;

import de.abas.erp.api.gui.TextBox;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ScreenEvent;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.common.type.enums.EnumDatabase;
import de.abas.erp.common.type.enums.EnumEditorAction;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.Query;
import de.abas.erp.db.SelectableObject;
import de.abas.erp.db.schema.company.Vartab;
import de.abas.erp.db.schema.custom.ersatzteileapp.KonfigurationEditor;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

import java.util.List;

@EventHandler(head = KonfigurationEditor.class)
@RunFopWith(EventHandlerRunner.class)
public class ConfigurationScreenHandler {

    /**
     * TBD.
     *
     * @param event               The event that occurred.
     * @param screenControl       The ScreenControl instance.
     * @param ctx                 The database context.
     * @param konfigurationEditor The KonfigurationEditor instance.
     * @throws EventException Throws EventException with error message, if editing
     *                        is not permitted for the current user.
     */
    @ScreenEventHandler(type = ScreenEventType.ENTER)
    public void screenEnter(ScreenEvent event, ScreenControl screenControl,
                            DbContext ctx, KonfigurationEditor konfigurationEditor) throws EventException {
        // checks whether screen is in edit mode
//        if (event.getCommand() == EnumEditorAction.Edit) {
        konfigurationEditor.setYspartdbnummer(getDatabaseNumberAsString(konfigurationEditor.getDBNo()));
        konfigurationEditor.setYspartdbname(konfigurationEditor.getDBDescr());
        String msg = konfigurationEditor.getDBNo().getDisplayString();

        String vartabSearchWord = "V-" + getDatabaseNumberAsString(konfigurationEditor.getDBNo()) + "-00";
        List<Vartab> selectVartab = getSelectList(Vartab.class, "swd", vartabSearchWord, ctx);

        konfigurationEditor.setYspartdbkommando(selectVartab.get(0).getDBCmd());
    }

    private String getDatabaseNumberAsString(EnumDatabase dbNoDb) {
        return dbNoDb.toString().replace("(", "").replace(")", "");
    }

    private <C extends SelectableObject> List<C> getSelectList(Class<C> type, String field, String value,
                                                               DbContext ctx) {
        SelectionBuilder<C> selectionBuilder = SelectionBuilder.create(type);
        selectionBuilder.add(Conditions.eq(field, value));
        Query<C> query = ctx.createQuery(selectionBuilder.build());
        return query.execute();
    }
}
