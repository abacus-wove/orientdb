/* Generated By:JJTree: Do not edit this line. OCollection.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.OCommandExecutionException;
import com.orientechnologies.orient.core.sql.executor.OResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OCollection extends SimpleNode {
  protected List<OExpression> expressions = new ArrayList<OExpression>();

  public OCollection(int id) {
    super(id);
  }

  public OCollection(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor.
   **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    builder.append("[");
    boolean first = true;
    for (OExpression expr : expressions) {
      if (!first) {
        builder.append(", ");
      }
      expr.toString(params, builder);
      first = false;
    }
    builder.append("]");
  }

  public void add(OExpression exp){
    this.expressions.add(exp);
  }

  public Object execute(OIdentifiable iCurrentRecord, OCommandContext ctx) {
    List<Object> result = new ArrayList<Object>();
    for (OExpression exp : expressions) {
      result.add(exp.execute(iCurrentRecord, ctx));
    }
    return result;
  }

  public Object execute(OResult iCurrentRecord, OCommandContext ctx) {
    List<Object> result = new ArrayList<Object>();
    for (OExpression exp : expressions) {
      result.add(exp.execute(iCurrentRecord, ctx));
    }
    return result;
  }

  public boolean needsAliases(Set<String> aliases) {
    for (OExpression expr : this.expressions) {
      if (expr.needsAliases(aliases)) {
        return true;
      }
    }
    return false;
  }

  public boolean isAggregate() {
    for (OExpression exp : this.expressions) {
      if (exp.isAggregate()) {
        return true;
      }
    }
    return false;
  }

  public OCollection splitForAggregation(AggregateProjectionSplit aggregateProj) {
    if (isAggregate()) {
      OCollection result = new OCollection(-1);
      for (OExpression exp : this.expressions) {
        if (exp.isAggregate() || exp.isEarlyCalculated()) {
          result.expressions.add(exp.splitForAggregation(aggregateProj));
        } else {
          throw new OCommandExecutionException("Cannot mix aggregate and non-aggregate operations in a collection: " + toString());
        }
      }
      return result;
    } else {
      return this;
    }
  }

  public boolean isEarlyCalculated() {
    for (OExpression exp : expressions) {
      if (!exp.isEarlyCalculated()) {
        return false;
      }
    }
    return true;
  }

  public OCollection copy() {
    OCollection result = new OCollection(-1);
    result.expressions = expressions == null ? null : expressions.stream().map(x -> x.copy()).collect(Collectors.toList());
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OCollection that = (OCollection) o;

    if (expressions != null ? !expressions.equals(that.expressions) : that.expressions != null)
      return false;

    return true;
  }

  @Override public int hashCode() {
    return expressions != null ? expressions.hashCode() : 0;
  }
}
/* JavaCC - OriginalChecksum=c93b20138b2ae58c5f76e458c34b5946 (do not edit this line) */
