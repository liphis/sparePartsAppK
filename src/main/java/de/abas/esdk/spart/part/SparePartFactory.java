package de.abas.esdk.spart.part;

import de.abas.eks.jfop.CommandException;
import de.abas.eks.jfop.remote.FO;
import de.abas.erp.db.DbContext;
import de.abas.erp.db.schema.custom.ersatzteileapp.Ersatzteil;
import de.abas.erp.db.schema.custom.ersatzteileapp.ErsatzteilEditor;
import de.abas.erp.db.schema.part.ProductEditor;
import de.abas.erp.db.schema.vendor.Vendor;
import org.w3c.dom.events.EventException;

public class SparePartFactory {

    Ersatzteil createNewSparePart(DbContext ctx, ProductEditor productEditor) {
        try {
            ErsatzteilEditor ersatzteilEditor = ctx.newObject(ErsatzteilEditor.class);
            ersatzteilEditor.setSwd("SP" + productEditor.getSwd());
            ersatzteilEditor.setDescr("Ersatzteil zu Artikel " + productEditor.getIdno());
            ersatzteilEditor.setYspartartikel(productEditor);
            ersatzteilEditor.setYspartlieferant((Vendor) productEditor.getVendor());
            ersatzteilEditor.setYspartliefnummer(productEditor.getPOrderNo());
            ersatzteilEditor.commit();
            productEditor.setYspartersatzteiltx(ersatzteilEditor.objectId().toString());
            return ersatzteilEditor.objectId();
        } catch (CommandException e) {
            FO.box("Fehler", "Ersatzteil konnte nicht angelegt werden!");
        }
        return null;
    }

}
