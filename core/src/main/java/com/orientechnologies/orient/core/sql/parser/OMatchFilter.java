/* Generated By:JJTree: Do not edit this line. OMatchFilter.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import java.util.ArrayList;
import java.util.List;

public class OMatchFilter extends SimpleNode {
  // TODO transform in a map
  protected List<OMatchFilterItem> items = new ArrayList<OMatchFilterItem>();

  public OMatchFilter(int id) {
    super(id);
  }

  public OMatchFilter(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor. *
   */
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public String getAlias() {
    for (OMatchFilterItem item : items) {
      if (item.alias != null) {
        return item.alias.getValue();
      }
    }
    return null;
  }

  public void setAlias(String alias) {
    boolean found = false;
    for (OMatchFilterItem item : items) {
      if (item.alias != null) {
        item.alias = new OIdentifier(-1);
        item.alias.setValue(alias);
        found = true;
        break;
      }
    }
    if (!found) {
      OMatchFilterItem newItem = new OMatchFilterItem(-1);
      newItem.alias = new OIdentifier(-1);
      newItem.alias.setValue(alias);
      items.add(newItem);
    }
  }

  public OWhereClause getFilter() {
    for (OMatchFilterItem item : items) {
      if (item.filter != null) {
        return item.filter;
      }
    }
    return null;
  }

  public String getClassName() {
    for (OMatchFilterItem item : items) {
      if (item.className != null) {
        if (item.className.value instanceof String)
          return (String) item.className.value;
        else
          return item.className.value.toString();
        // TODO evaluate expression
      }
    }
    return null;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append("{");
    boolean first = true;
    for (OMatchFilterItem item : items) {
      if (!first) {
        result.append(", ");
      }
      result.append(item.toString());
      first = false;
    }
    result.append("}");
    return result.toString();
  }

}
/* JavaCC - OriginalChecksum=6b099371c69e0d0c1c106fc96b3072de (do not edit this line) */
