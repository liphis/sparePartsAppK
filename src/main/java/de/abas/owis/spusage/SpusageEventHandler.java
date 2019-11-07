package de.abas.owis.spusage;

import de.abas.erp.api.gui.TextBox;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.FieldEventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ButtonEvent;
import de.abas.erp.axi2.event.FieldEvent;
import de.abas.erp.axi2.event.ScreenEvent;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.axi2.type.FieldEventType;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.common.type.AbasDate;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.EditorAction;
import de.abas.erp.db.exception.CommandException;
import de.abas.erp.db.infosystem.custom.owis.Spusage;
import de.abas.erp.db.schema.custom.ersatzteileapp.Ersatzteil;
import de.abas.erp.db.schema.custom.ersatzteileapp.ErsatzteilEditor;
import de.abas.erp.db.schema.userenums.UserEnumVerwendungsgrund;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.RowSelectionBuilder;
import de.abas.erp.db.RowQuery;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = Spusage.class, row = Spusage.Row.class)

@RunFopWith(EventHandlerRunner.class)

public class SpusageEventHandler {

    @ScreenEventHandler(type = ScreenEventType.ENTER)
    public void screenEnter(ScreenEvent event, ScreenControl screenControl, DbContext ctx, Spusage head) throws EventException {
        head.table().clear();
    }

    @ButtonEventHandler(field = "start", type = ButtonEventType.AFTER)
    public void startAfter(ButtonEvent event, ScreenControl screenControl, DbContext ctx, Spusage head) throws EventException {
        head.table().clear();
        RowSelectionBuilder<Ersatzteil, Ersatzteil.Row> selectionBuilder = RowSelectionBuilder.create(Ersatzteil.class, Ersatzteil.Row.class);
        if (head.getYspartersatz() != null) {
            selectionBuilder.addForHead(Conditions.eq(Ersatzteil.META.id, head.getYspartersatz().getId()));
        }
        if (head.getYspartartikel() != null) {
            selectionBuilder.addForHead(Conditions.eq(Ersatzteil.META.yspartartikel, head.getYspartartikel()));
        }
        selectionBuilder.add(Conditions.between(Ersatzteil.Row.META.yspartverwdatum, head.getYspartletztverwvon(), head.getYspartletztverwbis()));
        selectionBuilder.add(Conditions.eq(Ersatzteil.Row.META.yspartverwgrund, head.getYspartverwgrund()));
        RowQuery<Ersatzteil, Ersatzteil.Row> queryErsatzteilHeadTable = ctx.createQuery(selectionBuilder.build());
        if (queryErsatzteilHeadTable != null) {
            for (Ersatzteil.Row row : queryErsatzteilHeadTable) {
                Spusage.Row spRow = head.table().appendRow();
                spRow.setYsparttartikel(row.header().getYspartartikel());
                spRow.setYsparttersatz((Ersatzteil) row.header().getId());
                spRow.setYsparttletztverw(row.getYspartverwdatum());
                spRow.setYsparttletztverwgr(row.getYspartverwgrund());
                spRow.setYsparttverwneu(new AbasDate());
            }
        }

    }

    @FieldEventHandler(field = "yspartletztverwbis", type = FieldEventType.EXIT)
    public void yspartletztverwbisExit(FieldEvent event, ScreenControl screenControl, DbContext ctx, Spusage head) throws EventException {
        if (head.getYspartletztverwbis() != null && head.getYspartletztverwvon() != null) {
            if (head.getYspartletztverwbis().toDate().before(head.getYspartletztverwvon().toDate())) {
                head.setYspartletztverwbis(null);
                TextBox error = new TextBox(ctx, "Error", "Verwendung Bis darf nicht vor Verwendung von liegen.");
                error.show();
            }
        }
    }

    @FieldEventHandler(field = "yspartletztverwvon", type = FieldEventType.EXIT)
    public void yspartletztverwvonExit(FieldEvent event, ScreenControl screenControl, DbContext ctx, Spusage head) throws EventException {
        {
            if (head.getYspartletztverwbis() != null && head.getYspartletztverwvon() != null) {
                if (head.getYspartletztverwvon().toDate().after(head.getYspartletztverwbis().toDate())) {
                    head.setYspartletztverwvon(null);
                    TextBox error = new TextBox(ctx, "Error", "Verwendung Bis darf nicht vor Verwendung von liegen.");
                    error.show();

                }
            }
        }
    }


    // Tableevent
    @ButtonEventHandler(field = "yspartterfverw", type = ButtonEventType.AFTER, table = true)
    public void yspartterfverwAfter(ButtonEvent event, ScreenControl screenControl, DbContext ctx, Spusage head, Spusage.Row currentRow) throws EventException {
        if (currentRow.getYsparttverwneu() != null && !currentRow.getYsparttgrundneu().equals(UserEnumVerwendungsgrund.Empty)) {
            ErsatzteilEditor ersatzteilEditor = currentRow.getYsparttersatz().createEditor();
			try {
				ersatzteilEditor.open(EditorAction.UPDATE);
				ErsatzteilEditor.Row ersatzteilEditorRow = ersatzteilEditor.table().appendRow();
				ersatzteilEditorRow.setYspartverwdatum(currentRow.getYsparttverwneu());
				ersatzteilEditorRow.setYspartverwgrund(currentRow.getYsparttgrundneu());
				ersatzteilEditor.commit();
				this.startAfter(event, screenControl, ctx, head);
			}catch(CommandException cmdException) {
				TextBox textBox = new TextBox(ctx, "Error", "Ersatzteil konnte nicht bearbeitet werden.");
				textBox.show();
			}finally {
				if(ersatzteilEditor.active()){
					ersatzteilEditor.abort();
				}
			}
        }
    }

}
