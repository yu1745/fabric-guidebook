package appengx.libs.mdast.gfm.model;

import appengx.libs.mdast.model.MdAstAnyContent;
import appengx.libs.mdast.model.MdAstParent;

public class GfmTableRow extends MdAstParent<GfmTableCell> implements MdAstAnyContent {
    public GfmTableRow() {
        super("tableRow");
    }

    @Override
    protected Class<GfmTableCell> childClass() {
        return GfmTableCell.class;
    }
}
