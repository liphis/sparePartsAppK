package de.abas.esdk.spart.part;

import de.abas.eks.jfop.remote.FO;
import de.abas.erp.axi.event.EventException;
import de.abas.erp.axi.screen.ScreenControl;
import de.abas.erp.axi2.EventHandlerRunner;
import de.abas.erp.axi2.annotation.ButtonEventHandler;
import de.abas.erp.axi2.annotation.EventHandler;
import de.abas.erp.axi2.annotation.ScreenEventHandler;
import de.abas.erp.axi2.event.ButtonEvent;
import de.abas.erp.axi2.event.ScreenEvent;
import de.abas.erp.axi2.type.ButtonEventType;
import de.abas.erp.axi2.type.ScreenEventType;
import de.abas.erp.common.type.Id;
import de.abas.erp.common.type.enums.EnumEditorAction;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.custom.ersatzteileapp.Ersatzteil;
import de.abas.erp.db.schema.custom.ersatzteileapp.ErsatzteilEditor;
import de.abas.erp.db.schema.part.Product;
import de.abas.erp.db.schema.part.ProductEditor;
import de.abas.erp.db.schema.part.SelectablePart;
import de.abas.erp.db.selection.Conditions;
import de.abas.erp.db.selection.SelectionBuilder;
import de.abas.erp.db.util.QueryUtil;
import de.abas.erp.jfop.rt.api.annotation.RunFopWith;

@EventHandler(head = ProductEditor.class, row = ProductEditor.Row.class)

@RunFopWith(EventHandlerRunner.class)

public class ProductEventHandler {

    @ButtonEventHandler(field = "ysparterzatzteil", type = ButtonEventType.BEFORE)
    public void ysparterzatzteilBefore(ButtonEvent event, ScreenControl screenControl, DbContext ctx, ProductEditor head) throws EventException {
        if (!head.getYspartersatzteiltx().isEmpty()) {
            FO.box("Fehler", "Es wurde bereits ein Ersatzteil für diesen Artikel angelegt!");
            throw new EventException(1);
        }
        if(!event.getCommand().equals(EnumEditorAction.Edit)){
			FO.box("Fehler", "Anlage nur im Bearbeiten-Modus erlaubt!");
			throw new EventException(1);
		}
    }

    @ButtonEventHandler(field = "ysparterzatzteil", type = ButtonEventType.AFTER)
    public void ysparterzatzteilAfter(ButtonEvent event, ScreenControl screenControl, DbContext ctx, ProductEditor
            head) throws EventException {
		createNewSparePart(ctx, head);
    }

	private void createNewSparePart(DbContext ctx, ProductEditor head) {
		SparePartFactory sparePartFactory = new SparePartFactory();
		head.setYspartersatzteil(sparePartFactory.createNewSparePart(ctx,head));
	}

	@ScreenEventHandler(type = ScreenEventType.ENTER)
    public void screenEnter(ScreenEvent event, ScreenControl screenControl, DbContext ctx, ProductEditor head) {
        checkIfSparePartHasToBeFilled(head, ctx);
    }

    private void checkIfSparePartHasToBeFilled(ProductEditor head, DbContext ctx) {
        if (!head.getYspartersatzteiltx().isEmpty()) {
            Ersatzteil ersatzteil = addReferenceSparePartIntoScreen(ctx, head.getId());
            if (ersatzteil != null) {
                head.setYspartersatzteil(ersatzteil);
            }
        }
    }

    private Ersatzteil addReferenceSparePartIntoScreen(DbContext ctx, SelectablePart id) {
        Ersatzteil ersatzteil;
        SelectionBuilder<Ersatzteil> sparePartSelectionBuilder = SelectionBuilder.create(Ersatzteil.class);
        sparePartSelectionBuilder.add(Conditions.eq(Ersatzteil.META.yspartartikel, (Product) id));

        ersatzteil = QueryUtil.getFirst(ctx, sparePartSelectionBuilder.build());

        return ersatzteil;
    }
}
