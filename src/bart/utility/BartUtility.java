package bart.utility;

import bart.model.algebra.operators.ITupleIterator;
import bart.model.database.AttributeRef;
import bart.model.database.Cell;
import bart.model.database.ConstantValue;
import bart.model.database.IValue;
import bart.model.database.NullValue;
import bart.model.dependency.Dependency;
import bart.model.dependency.FormulaVariable;
import bart.model.database.Tuple;
import bart.model.database.TupleOID;
import bart.model.database.mainmemory.datasource.IDataSourceNullValue;
import bart.model.database.mainmemory.datasource.INode;
import bart.model.database.mainmemory.datasource.IntegerOIDGenerator;
import bart.model.database.mainmemory.datasource.nodes.AttributeNode;
import bart.model.database.mainmemory.datasource.nodes.LeafNode;
import bart.model.database.mainmemory.datasource.nodes.MetadataNode;
import bart.model.database.mainmemory.datasource.nodes.SequenceNode;
import bart.model.database.mainmemory.datasource.nodes.SetNode;
import bart.model.database.mainmemory.datasource.nodes.TupleNode;
import bart.BartConstants;
import bart.model.EGTask;
import bart.model.algebra.operators.StringComparator;
import bart.model.database.Attribute;
import bart.model.database.CellRef;
import bart.model.database.IDatabase;
import bart.model.database.ITable;
import bart.model.database.TableAlias;
import bart.model.errorgenerator.CellChange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;

public class BartUtility {

    /////////////////////////////////////   COLLECTIONS METHODS   /////////////////////////////////////
    @SuppressWarnings("unchecked")
    public static void addIfNotContained(List list, Object object) {
        if (!list.contains(object)) {
            list.add(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addAllIfNotContained(List dst, Collection src) {
        for (Object object : src) {
            addIfNotContained(dst, object);
        }
    }

    @SuppressWarnings("unchecked")
    public static void addIfNotNull(List list, Object object) {
        if (object != null) {
            list.add(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean equalLists(List list1, List list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        List list2Clone = new ArrayList(list2);
        for (Object o : list1) {
            if (!list2Clone.contains(o)) {
                return false;
            } else {
                list2Clone.remove(o);
            }
        }
        return (list2Clone.isEmpty());
    }

    @SuppressWarnings("unchecked")
    public static boolean areEqualConsideringOrder(List listA, List listB) {
        return !areDifferentConsideringOrder(listA, listB);
    }

    @SuppressWarnings("unchecked")
    public static boolean areDifferentConsideringOrder(List listA, List listB) {
        if (listA.size() != listB.size()) {
            return true;
        }
        for (int i = 0; i < listA.size(); i++) {
            Object valueA = listA.get(i);
            Object valueB = listB.get(i);
            if (!valueA.equals(valueB)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean contained(Collection list1, Collection list2) {
        if (list1.isEmpty()) {
            return true;
        }
        return (list2.containsAll(list1));
    }

    /////////////////////////////////////   PRINT METHODS   /////////////////////////////////////
    public static String printCollection(Collection l) {
        return printCollection(l, "");
    }

    @SuppressWarnings("unchecked")
    public static String printCollectionSorted(Collection l, String indent) {
        List sortedCollection = new ArrayList(l);
        Collections.sort(sortedCollection);
        return printCollection(sortedCollection, indent);
    }

    public static String printCollection(Collection l, String indent) {
        if (l == null) {
            return indent + "(null)";
        }
        if (l.isEmpty()) {
            return indent + "(empty collection)";
        }
        StringBuilder result = new StringBuilder();
        for (Object o : l) {
            result.append(indent).append(o).append("\n");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static String printDependencyIds(List<Dependency> dependencies) {
        StringBuilder result = new StringBuilder();
        for (Dependency dependency : dependencies) {
            result.append(dependency.getId()).append(" ");
        }
        return result.toString();
    }

    @SuppressWarnings("unchecked")
    public static String printMap(Map m) {
        String indent = "    ";
        StringBuilder result = new StringBuilder("----------------------------- MAP (size =").append(m.size()).append(") ------------\n");
        List<Object> keys = new ArrayList<Object>(m.keySet());
        Collections.sort(keys, new StringComparator());
        for (Object key : keys) {
            result.append("***************** Key ******************\n").append(key).append("\n");
            Object value = m.get(key);
            result.append(indent).append("---------------- Value ---------------------\n");
            if (value instanceof Collection) {
                result.append("size: ").append(((Collection) value).size()).append("\n");
                result.append(printCollection((Collection) value, indent)).append("\n");
            } else {
                result.append(indent).append(value).append("\n");
            }
        }
        return result.toString();
    }

    public static String printVariablesWithOccurrences(List<FormulaVariable> variables) {
        StringBuilder result = new StringBuilder();
        for (FormulaVariable formulaVariable : variables) {
            result.append(formulaVariable.toLongString()).append("\n");
        }
        return result.toString();
    }

    public static String printTupleIterator(Iterator<Tuple> iterator) {
        StringBuilder result = new StringBuilder();
        int counter = 0;
        while (iterator.hasNext()) {
            Tuple tuple = iterator.next();
            result.append(tuple.toStringWithAlias()).append("\n");
            counter++;
        }
        result.insert(0, "Number of tuples: " + counter + "\n");
        return result.toString();
    }

    public static String printCellChangesToLongString(List<CellChange> cellChanges) {
        if (cellChanges == null) {
            return "(null)";
        }
        if (cellChanges.isEmpty()) {
            return "(empty collection)";
        }
        StringBuilder result = new StringBuilder();
        for (CellChange cellChange : cellChanges) {
            result.append(cellChange.toLongString()).append("\n");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static void removeChars(int charsToRemove, StringBuilder result) {
        if (charsToRemove > result.length()) {
            throw new IllegalArgumentException("Unable to remove " + charsToRemove + " chars from a string with " + result.length() + " char!");
        }
        result.delete(result.length() - charsToRemove, result.length());
    }

    public static String printIterator(ITupleIterator iterator) {
        StringBuilder result = new StringBuilder();
        while (iterator.hasNext()) {
            result.append(BartConstants.INDENT).append(iterator.next().toStringWithOID()).append("\n");
        }
        iterator.reset();
        return result.toString();
    }

    public static Tuple createTuple(INode tupleNode, String tableName) {
        TupleOID tupleOID = new TupleOID(tupleNode.getValue());
        Tuple tuple = new Tuple(tupleOID);
        Cell oidCell = new Cell(tupleOID, new AttributeRef(tableName, BartConstants.OID), new ConstantValue(tupleOID));
        tuple.addCell(oidCell);
        for (INode attributeNode : tupleNode.getChildren()) {
            String attributeName = attributeNode.getLabel();
            Object attributeValue = attributeNode.getChild(0).getValue();
            IValue value;
            if (attributeValue instanceof IDataSourceNullValue) {
                value = new NullValue(attributeValue);
            } else if (attributeValue instanceof NullValue) {
                value = (NullValue) attributeValue;
            } else {
                value = new ConstantValue(attributeValue);
            }
            Cell cell = new Cell(tupleOID, new AttributeRef(tableName, attributeName), value);
            tuple.addCell(cell);
        }
        return tuple;
    }

    ///////////////////////// MAIN MEMORY INSTANCES METHODS
    public static INode createRootNode(INode node) {
        String type = node.getClass().getSimpleName();
        INode rootNode = createNode(type, node.getLabel(), IntegerOIDGenerator.getNextOID());
        rootNode.setRoot(true);
        return rootNode;
    }

    public static INode createNode(String nodeType, String label, Object value) {
        if (nodeType.equals("SetNode")) {
            return new SetNode(label, value);
        }
        if (nodeType.equals("TupleNode")) {
            return new TupleNode(label, value);
        }
        if (nodeType.equals("SequenceNode")) {
            return new SequenceNode(label, value);
        }
        if (nodeType.equals("AttributeNode")) {
            return new AttributeNode(label, value);
        }
        if (nodeType.equals("MetadataNode")) {
            return new MetadataNode(label, value);
        }
        if (nodeType.equals("LeafNode")) {
            return new LeafNode(label, value);
        }
        return null;
    }

    public static String removeRootLabel(String pathString) {
        return pathString.substring(pathString.indexOf(".") + 1);
    }

    public static String generateFolderPath(String filePath) {
        return FilenameUtils.getFullPath(filePath);
    }

    public static String generateSetNodeLabel() {
        return "Set";
    }

    public static String generateTupleNodeLabel() {
        return "Tuple";
    }

    public static String cleanConstantValue(String constant) {
        if (constant == null) {
            return null;
        }
        if (constant.startsWith("\"") && constant.endsWith("\"")) {
            return constant.substring(1, constant.length() - 1);
        }
        return constant;
    }

    public static IValue getOriginalOid(Tuple tuple, TableAlias tableAlias) {
        Cell oidCell = tuple.getCell(new AttributeRef(tableAlias, BartConstants.OID));
        return oidCell.getValue();
    }

    public static boolean isContainedInAll(String keyToSearch, List<Set<String>> list) {
        for (Set<String> otherEquivalenceClassesOID : list) {
            if (!otherEquivalenceClassesOID.contains(keyToSearch)) {
                return false;
            }
        }
        return true;
    }

    public static String getVioGenQueryKey(String dependencyId, String comparison) {
        return dependencyId + " " + comparison;
    }

    public static Attribute getAttribute(AttributeRef attributeRef, EGTask task) {
        return getTable(attributeRef, task).getAttribute(attributeRef.getName());
    }

    public static ITable getTable(AttributeRef attributeRef, EGTask task) {
        ITable table;
        if (attributeRef.isSource()) {
            table = task.getSource().getTable(attributeRef.getTableName());
        } else {
            table = task.getTarget().getTable(attributeRef.getTableName());
        }
        return table;
    }

    public static Attribute getAttribute(AttributeRef attributeRef, IDatabase db) {
        ITable table = db.getTable(attributeRef.getTableName());
        return table.getAttribute(attributeRef.getName());
    }

    public static String getDeltaRelationName(String tableName, String attributeName) {
        return tableName + BartConstants.DELTA_TABLE_SEPARATOR + attributeName;
    }

    public static CellRef getCellRefNoAlias(Cell cell) {
        AttributeRef attributeRefNoAlias = new AttributeRef(cell.getAttributeRef().getTableName(), cell.getAttribute());
        return new CellRef(cell.getTupleOID(), attributeRefNoAlias);
    }

    public static String getDirtyCloneSuffix(EGTask task) {
        if (task.getConfiguration().getCloneSuffix() != null) {
            return task.getConfiguration().getCloneSuffix();
        }
        return BartConstants.DIRTY_SUFFIX;
    }

}
