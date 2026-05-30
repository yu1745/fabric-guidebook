package appengx.libs.mdast.gfm.model;

import appengx.libs.mdast.model.MdAstAnyContent;
import appengx.libs.mdast.model.MdAstParent;
import appengx.libs.mdast.model.MdAstPhrasingContent;

public class GfmTableCell extends MdAstParent<MdAstPhrasingContent> implements MdAstAnyContent {
    public GfmTableCell() {
        super("tableCell");
    }

    @Override
    protected Class<MdAstPhrasingContent> childClass() {
        return MdAstPhrasingContent.class;
    }
}
