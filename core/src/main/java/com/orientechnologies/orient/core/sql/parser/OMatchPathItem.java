/* Generated By:JJTree: Do not edit this line. OMatchPathItem.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.orientechnologies.orient.core.sql.parser;

import com.orientechnologies.orient.core.command.OCommandContext;
import com.orientechnologies.orient.core.db.record.OIdentifiable;

import java.util.*;

public class OMatchPathItem extends SimpleNode {
  protected OMethodCall  method;
  protected OMatchFilter filter;

  public OMatchPathItem(int id) {
    super(id);
  }

  public OMatchPathItem(OrientSql p, int id) {
    super(p, id);
  }

  /**
   * Accept the visitor.
   **/
  public Object jjtAccept(OrientSqlVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }

  public boolean isBidirectional() {
    if (filter.getWhileCondition() != null) {
      return false;
    }
    if (filter.getMaxDepth() != null) {
      return false;
    }
    if (filter.isOptional()) {
      return false;
    }
    return method.isBidirectional();
  }

  public void toString(Map<Object, Object> params, StringBuilder builder) {
    method.toString(params, builder);
    if (filter != null) {
      filter.toString(params, builder);
    }
  }

  protected Iterable<OIdentifiable> executeTraversal(OMatchStatement.MatchContext matchContext, OCommandContext iCommandContext,
      OIdentifiable startingPoint, int depth) {

    OWhereClause filter = null;
    OWhereClause whileCondition = null;
    Integer maxDepth = null;
    if (this.filter != null) {
      filter = this.filter.getFilter();
      whileCondition = this.filter.getWhileCondition();
      maxDepth = this.filter.getMaxDepth();
    }

    Set<OIdentifiable> result = new HashSet<OIdentifiable>();

    if (whileCondition == null && maxDepth == null) {// in this case starting point is not returned and only one level depth is
      // evaluated
      Iterable<OIdentifiable> queryResult = traversePatternEdge(matchContext, startingPoint, iCommandContext);

      if (this.filter == null || this.filter.getFilter() == null) {
        return queryResult;
      }

      for (OIdentifiable origin : queryResult) {
        Object previousMatch = iCommandContext.getVariable("$currentMatch");
        iCommandContext.setVariable("$currentMatch", origin);
        if (filter == null || filter.matchesFilters(origin, iCommandContext)) {
          result.add(origin);
        }
        iCommandContext.setVariable("$currentMatch", previousMatch);
      }
    } else {// in this case also zero level (starting point) is considered and traversal depth is given by the while condition
      iCommandContext.setVariable("$depth", depth);
      Object previousMatch = iCommandContext.getVariable("$currentMatch");
      iCommandContext.setVariable("$currentMatch", startingPoint);
      if (filter == null || filter.matchesFilters(startingPoint, iCommandContext)) {
        result.add(startingPoint);
      }

      if ((maxDepth == null || depth < maxDepth) && (whileCondition == null || whileCondition
          .matchesFilters(startingPoint, iCommandContext))) {

        Iterable<OIdentifiable> queryResult = traversePatternEdge(matchContext, startingPoint, iCommandContext);

        for (OIdentifiable origin : queryResult) {
          // TODO consider break strategies (eg. re-traverse nodes)
          Iterable<OIdentifiable> subResult = executeTraversal(matchContext, iCommandContext, origin, depth + 1);
          if (subResult instanceof Collection) {
            result.addAll((Collection<? extends OIdentifiable>) subResult);
          } else {
            for (OIdentifiable i : subResult) {
              result.add(i);
            }
          }
        }
      }
      iCommandContext.setVariable("$currentMatch", previousMatch);
    }
    return result;
  }

  protected Iterable<OIdentifiable> traversePatternEdge(OMatchStatement.MatchContext matchContext, OIdentifiable startingPoint,
      OCommandContext iCommandContext) {

    Iterable possibleResults = null;
    if (filter != null) {
      OIdentifiable matchedNode = matchContext.matched.get(filter.getAlias());
      if (matchedNode != null) {
        possibleResults = Collections.singleton(matchedNode);
      } else if (matchContext.matched.containsKey(filter.getAlias())) {
        possibleResults = Collections.emptySet();//optional node, the matched element is a null value
      } else {
        possibleResults = matchContext.candidates == null ? null : matchContext.candidates.get(filter.getAlias());
      }
    }

    Object qR = this.method.execute(startingPoint, possibleResults, iCommandContext);
    return (qR instanceof Iterable) ? (Iterable) qR : Collections.singleton((OIdentifiable) qR);
  }

  @Override public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    OMatchPathItem that = (OMatchPathItem) o;

    if (method != null ? !method.equals(that.method) : that.method != null)
      return false;
    if (filter != null ? !filter.equals(that.filter) : that.filter != null)
      return false;

    return true;
  }

  @Override public int hashCode() {
    int result = method != null ? method.hashCode() : 0;
    result = 31 * result + (filter != null ? filter.hashCode() : 0);
    return result;
  }

  @Override public OMatchPathItem copy() {
    OMatchPathItem result = null;
    try {
      result = getClass().getConstructor(Integer.TYPE).newInstance(-1);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    result.method = method == null ? null : method.copy();
    result.filter = filter == null ? null : filter.copy();
    return result;
  }


}
/* JavaCC - OriginalChecksum=ffe8e0ffde583d7b21c9084eff6a8944 (do not edit this line) */
