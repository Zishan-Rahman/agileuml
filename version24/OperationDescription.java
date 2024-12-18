import java.util.Vector;
import java.util.List;
import java.util.ArrayList; 
import java.io.*;

/* Package: EIS */ 
/******************************
* Copyright (c) 2003--2022 Kevin Lano
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
* *****************************/

public class OperationDescription extends BehaviouralFeature
{ private Vector maintainOps = new Vector(); // extra ops for Dbi, in case of set
  String opaction = ""; 
  String oprole = ""; 


  /* Operation descriptions are EIS use cases: 
     create E: parameters are E's attributes
     list E: no parameters
     delete E: primary keys of E
     get E: primary keys of E
     searchBy E f: f (attribute of E)
     set E f: f + primary keys of E
     edit E: attributes of E
     add E r: keys of E and of F, where r : F 
     remove E r: keys of E and keys of F, where r : F
   */ 
  	
  	
  public OperationDescription(String nme,
                              Entity e, String op, String role)
  { // E is the entity being operated on, op the 
    // kind of op: create, delete, add, etc
    super(nme,new Vector(),true,null);
    setEntity(e);
    setStereotypes(new Vector()); 
    stereotypes.add(op);
    opaction = op; 
    if (role != null && !(role.equals("")))
    { stereotypes.add(role); } 
    oprole = role; 

    System.out.println(">>> Action: " + op + " Stereotypes: " + stereotypes); 
      
    if (op.equals("create"))
    { setParameters(e.getAttributes()); }
    if (op.equals("searchBy"))
    { Attribute att = e.getAttribute(role); 
      if (att != null)
      { Vector pars = new Vector(); 
        pars.add(att); 
        setParameters(pars);
      } 
    }
    else if (op.equals("set"))
    { Vector pars = new Vector(); 
      Attribute att = e.getAttribute(role); 
      if (att != null)
      { Vector uniq = e.getUniqueAttributes();
        if (uniq.contains(att))
        { System.err.println("!! ERROR: Cannot define set on key " + att); } 
        else 
        { pars.add(att);
          pars.addAll(uniq);   // put at end because of SQL UPDATE
          setParameters(pars); 
        }
      }
    }         
    else if (op.equals("edit"))
    { Vector epars = e.getAttributes(); 
      Vector pars = new Vector(); 
      Vector uniq = e.getUniqueAttributes(); 
      pars.addAll(epars); 
      pars.removeAll(uniq); 
      pars.addAll(uniq); // so they are at end -- but why???
      setParameters(pars); 
    }
    else if (op.equals("list")) // no parameters
    { } 
    else if (op.equals("get") || op.equals("delete")) // delete, get
    { Vector keys = e.getUniqueAttributes();
      if (keys.size() == 0)
      { System.err.println("!! ERROR: Cannot define operation get/delete: no primary key"); 
        return; 
      }
      else 
      { setParameters(keys); } 
    }
    else if (op.equals("add") || op.equals("remove"))
    { Association ast = entity.getRole(role); 

      System.out.println(">>> " + role + " should be Many,\n>>> and the association 1-* or 0..1-* and persistent.\n>>> A foreign key should exist in the target class"); 
      System.out.println(); 

      if (ast == null) 
      { System.err.println("!! ERROR: not a valid role: " + role); 
        return; 
      }

      if (ast.isPersistent()) { } 
      else 
      { System.err.println("!! ERROR: not a persistent association");
        return; 
      } 

      if (ast.isOneMany() || ast.isZeroOneMany())
      { Entity entity2 = ast.getEntity2(); 
        Vector bkeys = entity2.getUniqueAttributes(); 
        if (bkeys.size() == 0) 
        { System.err.println("!! ERROR: no primary keys for: " + entity2); }  
        Vector pars = new Vector(); 
        pars.addAll(e.getUniqueAttributes()); 
        pars.addAll(bkeys); 
        setParameters(pars); 
      }
      else 
      { System.err.println("!! ERROR: not a valid 1-* or 0..1-* association"); }  
    }
  } // add, remove: e's key and entity2 key. 
    // check: e key + some atts
  
  public static Vector allCoreOperations(Vector entities)
  { // createE, editE, deleteE, listE, searchByattE
    Vector res = new Vector(); 
	
    for (int i = 0; i < entities.size(); i++) 
    { Entity ent = (Entity) entities.get(i); 
      if (ent.isDerived() || ent.isComponent()) 
      { }
      else if (ent.isPersistent())
      { String ename = ent.getName(); 
        OperationDescription createE = new OperationDescription("create" + ename, ent, "create", null); 
        res.add(createE); 
        OperationDescription editE = new OperationDescription("edit" + ename, ent, "edit", null); 
        res.add(editE);
        OperationDescription listE = new OperationDescription("list" + ename, ent, "list", null); 
        res.add(listE);
        OperationDescription deleteE = new OperationDescription("delete" + ename, ent, "delete", null); 
        res.add(deleteE);
		
        Vector attrs = ent.getAttributes(); 
        for (int j = 0; j < attrs.size(); j++) 
        { Attribute att = (Attribute) attrs.get(j);
          Type atttyp = att.getType(); 
          if (atttyp.isCollection() || 
               atttyp.isMap() || 
               atttyp.isFunction()) 
          { continue; } 

          String attname = att.getName();  
          OperationDescription searchByEatt = new OperationDescription("searchBy" + ename + attname, ent, "searchBy", attname); 
          res.add(searchByEatt); 
        }

        Vector assocs = ent.getAssociations(); 

        for (int j = 0; j < assocs.size(); j++) 
        { Association ast = (Association) assocs.get(j); 

          if (ast.isPersistent()) { } 
          else 
          { continue; }
 
          if (ast.isOneMany() || ast.isZeroOneMany()) 
          { String role = ast.getRole2(); 
            OperationDescription addErole = 
               new OperationDescription(
                          "add" + ename + role,
                          ent, "add", role);
            res.add(addErole);  
            OperationDescription removeErole = 
               new OperationDescription(
                          "remove" + ename + role,
                          ent, "remove", role);
            res.add(removeErole);  
          } // ast should be persistent
        } 
      }
    }
    

    return res; 
  }

  public void addDbiMaintainOps(Vector ops)
  { maintainOps.addAll(ops); } 

  public String getMaintainOps()
  { String res = ""; 
    for (int i = 0; i < maintainOps.size(); i++) 
    { res = res + maintainOps.get(i); }
    return res; 
  }  

  public String getAction()
  { if (stereotypes.size() > 0)
    { return (String) stereotypes.get(0); } 
    return opaction; 
  } // should be the first one that isn't a standard stereotype

  public void saveData(PrintWriter out)
  { String nme = getName();
    String ename = getEntityName();
    String stereos = ""; 
    for (int p = 0; p < stereotypes.size(); p++) 
    { stereos = stereos + " " + stereotypes.get(p); }
    out.println("UseCase:");
    out.println(nme + " " + ename + " " + stereos);  // op + role
    out.println(); 
  }

  public void saveModelData(PrintWriter out, Vector saved)
  { String nme = getName();
    String ename = getEntityName();
    String stereos = ""; 


    out.println(nme + " : OperationDescription");
    out.println(nme + ".name = \"" + nme + "\""); 
    out.println(nme + ".owner = " + ename); 

    for (int p = 0; p < stereotypes.size(); p++) 
    { out.println("\"" + stereotypes.get(p) + "\" : " + nme + ".stereotypes"); }
 
    out.println(); 
  }

  public String getDbiParameterDec()
  { String res = ""; 
    Vector pars = getParameters(); 
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i); 
      String attnme = att.getName(); 
      Type typ = att.getType(); 
      if (typ.getName().equals("boolean"))
      { res = res + "String " + attnme; } 
      else
      { res = res + typ.getJava() + " " + attnme; } 
      if (i < pars.size() - 1)
      { res = res + ","; } 
    }
    return res; 
  }      

  public String getJSPDbiParameterDec()
  { String res = ""; 
    String ent = entity.getName();
    Vector pars = getParameters(); 
    
    if (opaction.startsWith("list"))
    { return res; }
    else if (opaction.startsWith("delete"))
    { Attribute att1 = (Attribute) pars.get(0); 
                  // entity.getPrincipalPrimaryKey(); 
      Type typ = att1.getType(); 
      String tname = typ.getJava8(); 
      res = tname + " _x"; 
    } 
    else if (opaction.startsWith("searchBy"))
    { Attribute att1 = (Attribute) pars.get(0); 
                  // entity.getPrincipalPrimaryKey(); 
      Type typ = att1.getType(); 
      String tname = typ.getJava8(); 
      res = tname + " _x"; 
    } 
    else if (opaction.startsWith("add") ||
             opaction.startsWith("remove"))
    { Attribute att1 = (Attribute) pars.get(0); 
                  // entity.getPrincipalPrimaryKey(); 
      Type typ1 = att1.getType(); 
      String tname1 = typ1.getJava8(); 
      Attribute att2 = (Attribute) pars.get(1); 
                  // entity2.getPrincipalPrimaryKey(); 
      Type typ2 = att2.getType(); 
      String tname2 = typ2.getJava8(); 
      res = tname1 + " _x, " + tname2 + " _y"; 
    } 
    else  
    { res = ent + "VO _x"; }  
    return res; 
  }      

  public String getJSPDbiParameterTransfer()
  { String res = ""; 
    String ent = entity.getName(); 
    Vector pars = getParameters(); 
    if (opaction.startsWith("delete") || 
        opaction.startsWith("searchBy"))
    { Attribute att1 = (Attribute) pars.get(0); 
                  // entity.getPrincipalPrimaryKey(); 
      String pk = att1.getName(); 
      Type typ = att1.getType(); 
      String tname = typ.getJava8(); 
      if (typ.getName().equals("boolean"))
      { tname = "String"; } 
      res = "    " + tname + " " + pk + " = _x;\n"; 
    } 
    else if (opaction.startsWith("add") || 
             opaction.startsWith("remove"))
    { Attribute att1 = (Attribute) pars.get(0); 
                  // entity.getPrincipalPrimaryKey(); 
      String pk1 = att1.getName(); 
      Type typ1 = att1.getType(); 
      String tname1 = typ1.getJava8(); 
      if (typ1.getName().equals("boolean"))
      { tname1 = "String"; }

      Attribute att2 = (Attribute) pars.get(1); 
                  // entity2.getPrincipalPrimaryKey(); 
      String pk2 = att2.getName(); 
      Type typ2 = att2.getType(); 
      String tname2 = typ2.getJava8(); 
      if (typ2.getName().equals("boolean"))
      { tname2 = "String"; } 
       
      res = "    " + tname1 + " " + pk1 + " = _x;\n" + 
            "    " + tname2 + " " + pk2 + " = _y;\n"; 
    } 
    else  
    { for (int i = 0; i < pars.size(); i++)
      { Attribute att = (Attribute) pars.get(i); 
        String attnme = att.getName(); 
        Type typ = att.getType(); 
        if (typ.getName().equals("boolean"))
        { res = res + "    String " + attnme + " = _x.get" + attnme + "();\n"; } 
        else
        { res = res + "    " + typ.getJava() + " " + attnme + " = _x.get" + attnme + "();\n"; }
      }  
    }
    return res; 
  }      

  private String getAndroidDbiCallList()
  { String res = "";
    String ent = entity.getName(); 
    Vector params = getParameters();
    Vector pars = entity.getAttributes();
 
    if (opaction.startsWith("delete"))
    { Attribute att1 = entity.getPrincipalPrimaryKey(); 
      return att1.getName(); 
    }
    else if (opaction.startsWith("list"))
    { return res; } 
    else if (opaction.startsWith("searchBy"))
    { Attribute att2 = (Attribute) params.get(0); 
      return att2.getName(); 
    }
    else if (opaction.startsWith("add") || 
             opaction.startsWith("remove"))
    { Attribute att1 = (Attribute) params.get(0); 
      Attribute att2 = (Attribute) params.get(1); 
      return att1.getName() + ", " + att2.getName(); 
    } 


    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      Type t = att.getType();
      String nme = att.getName();
      if ("int".equals(t.getName()) || "long".equals(t.getName()))
      { nme = "i" + nme; }
      else if ("double".equals(t.getName()))
      { nme = "d" + nme; }
      res = res + nme;
      if (i < pars.size() - 1)
      { res = res + ", "; }
    }

    // if (pars.size() <= 1) 
    // { return res; } 

    return "new " + ent + "VO(" + res + ")";
  }

  private String getDbiCallList()
  { String res = "";
    Vector pars = getParameters();
    
    if (opaction.startsWith("delete"))
    { // Attribute att1 = (Attribute) pars.get(0);
      Attribute att1 = entity.getPrincipalPrimaryKey();  
      return att1.getName(); 
    }

    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      Type t = att.getType();
      String nme = att.getName();
      if ("int".equals(t.getName()) || "long".equals(t.getName()))
      { nme = "i" + nme; }
      else if ("double".equals(t.getName()))
      { nme = "d" + nme; }
      res = res + nme;
      if (i < pars.size() - 1)
      { res = res + ", "; }
    }
    return res;
  }

  public String getAndroidDbiOpCall()
  { // Op is: dbi.action + ename(pars);
    String pars = getAndroidDbiCallList();
    return "dbi." + getODName() + "(" + pars + ");";
  } // plus the role name in case of get/add/remove
 
  public String getAndroidModelOpCall()
  { // Op is: model.action + ename(pars);
    String pars = getAndroidDbiCallList();
    return "model." + getODName() + "(" + pars + ");";
  } // plus the role name in case of get/add/remove
 
  public String getDbiOpCall()
  { // Op is: dbi.action + ename(pars);
    String pars = getDbiCallList();
    return "dbi." + getODName() + "(" + pars + ");";
  } // plus the role name in case of get/add/remove

  public String getODName()
  { String action = getStereotype(0);
    if (action.equals("query"))
    { return getODName(1); } 

    String ename = entity.getName();
    if (action.equals("get") || 
        action.equals("add") || action.equals("remove") ||
        action.equals("searchBy") || action.equals("set"))
    { return action + ename + getStereotype(1); } 
    else 
    { return action + ename; } 
  }

  public String getODName(int i)
  { if (i >= stereotypes.size()) 
    { return ""; } 
 
    String action = getStereotype(i);
    if (action.equals("query"))
    { return getODName(i+1); } 

    String ename = entity.getName();
    if (action.equals("get") || action.equals("add") || action.equals("remove") ||
        action.equals("searchBy") || action.equals("set"))
    { return action + ename + getStereotype(i+1); } 
    else 
    { return action + ename; } 
  }

  public void androidDbiOp(PrintWriter out) 
  { String action = getStereotype(0);
    String ename = entity.getName();
    ArrayList ents = new ArrayList();
    ents.add(ename);
    ArrayList flds = new ArrayList();
    Vector pars = getParameters();
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      flds.add(att.getName());
    }

    if (action.equals("create"))
    { androidDbiCreateOp(ename,out); } 
    else if (action.equals("delete"))
    { androidDbiDeleteOp(ename,out); } 
    else if (action.equals("list"))
    { androidDbiListOp(ename,out); } 
    else if (action.equals("searchBy") && stereotypes.size() > 1)
    { String key = getStereotype(1);
      androidDbiSearchByOp(ename,key,out); 
    }  
    else if (action.equals("edit"))
    { androidDbiEditOp(ename,out); } 
  } 

  public void androidDbiCreateOp(String ent, PrintWriter out)
  { String entlc = ent.toLowerCase();
    Vector atts = entity.getAttributes(); 
    int natts = atts.size();
    out.println("  public void create" + ent + "(" + ent + "VO " + entlc + "vo)");
    out.println("  { database = getWritableDatabase();");
    out.println("    ContentValues _wr = new ContentValues(" + ent + "_NUMBER_COLS);");

    for (int z = 0; z < natts; z++)
    { Attribute att = (Attribute) atts.get(z);
      String nmeatt = att.getName();   
      String nup = nmeatt.toUpperCase();
      out.println("    _wr.put(" + ent + "_COLS[" + ent + "_COL_" + nup + "]," + 
                  entlc + "vo.get" + nmeatt + "());");
    }
    out.println("    database.insert(" + ent + "_TABLE_NAME," + 
                                     ent + "_COLS[1],_wr);");
    out.println("  }");
  } 

  public void androidDbiDeleteOp(String ent, PrintWriter out)
  { String entlc = ent.toLowerCase();
    
    out.println("  public void delete" + ent + "(String " + entlc + "Id)"); 
    out.println("  { database = getWritableDatabase();"); 
    out.println("    String[] _args = new String[]{" + entlc + "Id};"); 
    out.println("    database.delete(" + ent + "_TABLE_NAME, \"" + entlc + "Id = ?\", _args);"); 
    out.println("  }"); 
  } 

  public void androidDbiListOp(String ent, PrintWriter out) 
  { Vector atts = entity.getAttributes(); 
    int natts = atts.size(); 
    String entlc = ent.toLowerCase();
    
    out.println("  public ArrayList<" + ent + "VO> list" + ent + "()");
    out.println("  { ArrayList<" + ent + "VO> res = new ArrayList<" + ent + "VO>();");
    out.println("    database = getReadableDatabase();");
    out.println("    Cursor cursor = database.query(" + ent +
                     "_TABLE_NAME," + ent + "_COLS,null,null,null,null,null);");
    out.println("    cursor.moveToFirst();");
    out.println("    while(!cursor.isAfterLast())");
    out.println("    { " + ent + "VO " + entlc + "vo = new " + ent + "VO();"); 
    for (int y = 0; y < natts; y++)
    { Attribute att = (Attribute) atts.get(y);
      String anme = att.getName();
      String getop = att.androidExtractOp(ent); 
      out.println("      " + entlc + "vo.set" + anme + "(" + getop + ");");
    }
    out.println("      res.add(" + entlc + "vo);");
    out.println("      cursor.moveToNext();");
    out.println("    }");
    out.println("    cursor.close();");
    out.println("    return res;");
    out.println("  }");
  } 

  public void androidDbiSearchByOp(String ent, String att, PrintWriter out) 
  { Vector atts = entity.getAttributes(); 

    int natts = atts.size(); 
    String entlc = ent.toLowerCase();

    out.println("  public ArrayList<" + ent + "VO> searchBy" + ent + att + "(String _val)");
    out.println("  { ArrayList<" + ent + "VO> res = new ArrayList<" + ent + "VO>();");
    out.println("    database = getReadableDatabase();");
    out.println("    String[] _args = new String[]{_val};");
    String allatts = "_id"; 
    for (int c = 0; c < natts; c++)
    { Attribute ax = (Attribute) atts.get(c);
      allatts = allatts + ", " + ax.getName(); 
    }
    out.println("    Cursor cursor = database.rawQuery(\"select " + allatts + " from " + 
                                                ent + " where " + att + " = ?\", _args);");
    out.println("    cursor.moveToFirst();");
    out.println("    while(!cursor.isAfterLast())");
    out.println("    { " + ent + "VO " + entlc + "vo = new " + ent + "VO();"); 
    for (int y = 0; y < natts; y++)
    { Attribute attx = (Attribute) atts.get(y);
      String anme = attx.getName();
      String getop = attx.androidExtractOp(ent);
      out.println("      " + entlc + "vo.set" + anme + "(" + getop + ");");
    }
    out.println("      res.add(" + entlc + "vo);");
    out.println("      cursor.moveToNext();");
    out.println("    }");
    out.println("    cursor.close();");
    out.println("    return res;"); 
    out.println("  }");
    out.println();
  } 

  public void androidDbiEditOp(String ent, PrintWriter out)
  { String entlc = ent.toLowerCase();
    Vector atts = entity.getAttributes(); 
    int natts = atts.size();

    out.println("  public void edit" + ent + "(" + ent + "VO " + entlc + "vo)");
    out.println("  { database = getWritableDatabase();");
    out.println("    ContentValues _wr = new ContentValues(" + ent + "_NUMBER_COLS);");
    for (int z = 0; z < natts; z++)
    { Attribute att = (Attribute) atts.get(z);
      String nmeatt = att.getName();   
      String nup = nmeatt.toUpperCase();
      out.println("    _wr.put(" + ent + "_COLS[" + ent + "_COL_" + nup + "]," + 
                               entlc + "vo.get" + nmeatt + "());");
    }
    out.println("    String[] _args = new String[]{ " + entlc + "vo.get" + entlc + "Id() };");
    out.println("    database.update(" + ent + "_TABLE_NAME, _wr, \"" + entlc + "Id=?\", _args);");
    out.println("  }");
  } 

  public SQLStatement getSQL0()
  { String action = getStereotype(0);
    String ename = entity.getName();
    ArrayList ents = new ArrayList();
    ents.add(ename);
    ArrayList flds = new ArrayList();
    Vector pars = getParameters();
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      flds.add(att.getName());
    }

    if (action.equals("create"))
    { return 
        new SQLStatement("INSERT",ents,flds,
                         new ArrayList(),"");
    }
    else if (action.equals("delete"))
    { return 
        new SQLStatement("DELETE",ents,flds,
                         new ArrayList(),"");
    }
    else if (action.equals("edit") || action.equals("set"))
    { Vector keys = entity.getUniqueAttributes();
      Vector pars2 = (Vector) pars.clone();
      pars2.removeAll(keys);
      String wre = SQLStatement.buildWhere0(
                      ModelElement.getNames(keys));
      return 
        new SQLStatement("UPDATE",ents,
                         ModelElement.getNames(pars2),
                         new ArrayList(),wre);
    }
    else if (action.equals("get"))
    { String role = getStereotype(1); 
      Association ast = entity.getRole(role); 
      if (ast == null) 
      { System.err.println("!! ERROR: No role named " + role + " for entity " + entity); 
        return null; 
      } 
      else 
      { Entity entity2 = ast.getEntity2(); 
        Vector bfeatures = ModelElement.getNames(entity2.getAttributes()); 
        Vector akeys = entity.getUniqueAttributes();      
        Attribute key = (Attribute) akeys.get(0);
        String akey0 = key.getName(); 
         
        String akey = ename + "." + akey0;   // A.akey
        ArrayList ents2 = new ArrayList(); 
        ents2.add(ename); 
        ents2.add(entity2.getName()); 
        ArrayList keys2 = new ArrayList(); 
        keys2.add(akey); 
        String wre2 = SQLStatement.buildWhere0(keys2);  // akey = ?
        Vector cons = ast.getConstraints(); 
        if (cons.size() > 0 && ast.isManyMany())
        { wre2 = ((Constraint) cons.get(0)).toSQL() + " AND " + wre2; }
        // AND of constraints
        else if (ast.getCard2() == MANY) // foreign key at entity2 end
        { String bkey = entity2.getName() + "." + akey0;
          bfeatures.remove(akey0); 
          bfeatures.add(bkey);
          wre2 = wre2 + " AND " + bkey + " = " + akey; 
        }
        else // foreign key at entity1 end
        { Vector bkeys = entity2.getUniqueAttributes(); 
          Attribute bkeyatt = (Attribute) bkeys.get(0);
          String bkeyname = bkeyatt.getName();  
          bfeatures.remove(bkeyname); 
          String bkey = entity2.getName() + "." + bkeyname; 
          bfeatures.add(bkeyname); 
          wre2 = wre2 + " AND " + bkey + " = " + ename + "." + bkeyname; 
        } 
        return new SQLStatement("SELECT",ents2,bfeatures,new ArrayList(), 
                                wre2); 
      } 
    } 
    else if (action.equals("list"))
    { Vector star = entity.getAttributes();  
      return new SQLStatement("SELECT",ents,ModelElement.getNames(star),
                              new ArrayList(),null); 
    } 
    else if (action.equals("searchBy"))
    { Vector star = entity.getAttributes(); 
      SQLStatement stat = 
        new SQLStatement("SELECT",ents,
                         ModelElement.getNames(star),
                         new ArrayList(),null);
      Vector atts = new Vector(); 
      atts.add(getStereotype(1)); 
      stat.buildWhere(atts); 
      return stat; 
    } 
    else if (action.equals("add"))
    { String role = getStereotype(1); 
      Association ast = entity.getRole(role); 
      if (ast == null) 
      { System.err.println("!! ERROR: No role named " + role + " for entity " + entity); 
        return null; 
      } 
      else // Assume ONE-MANY or ZEROONE-MANY
      { Entity entity2 = ast.getEntity2(); 
        Attribute akeyatt = 
           (Attribute) getParameters().get(0);  
        Vector bkeys = 
          ModelElement.getNames(
                 entity2.getUniqueAttributes());
        if (akeyatt == null || bkeys.size() == 0)
        { return null; } 
        String bkey = (String) bkeys.get(0); 
        String akey = akeyatt.getName(); 
        bkey = entity2.getName() + "." + bkey; 
        akey = entity2.getName() + "." + akey; 
        ArrayList ents2 = new ArrayList(); 
        ents2.add(entity2.getName()); 
        ArrayList forkeys = new ArrayList(); 
        forkeys.add(akey); 
        ArrayList localkeys = new ArrayList(); 
        localkeys.add(bkey); 
        String wre = SQLStatement.buildWhere0(localkeys); 
        return new SQLStatement("UPDATE",ents2,
                                forkeys,new ArrayList(),wre); 
      }
    } 
    else if (action.equals("remove"))
    { String role = getStereotype(1); 
      Association ast = entity.getRole(role); 
      if (ast == null) 
      { System.err.println("!! ERROR: No role named " + role + " for entity " + entity); 
        return null; 
      } 
      else  // assume it is ONE-MANY
      { Entity entity2 = ast.getEntity2(); 
        Attribute akeyatt = 
           (Attribute) getParameters().get(0);  
        Vector bkeys = ModelElement.getNames(
                         entity2.getUniqueAttributes());
        if (akeyatt == null || bkeys.size() == 0)
        { return null; } 

        String bkey = (String) bkeys.get(0);
        String akey = akeyatt.getName(); 
        bkey = entity2.getName() + "." + bkey; 
        akey = entity2.getName() + "." + akey; 
        ArrayList ents2 = new ArrayList(); 
        ents2.add(entity2.getName()); 
        ArrayList localkeys = new ArrayList(); 
        // localkeys.add(akey); 
        localkeys.add(bkey); 
        String wre = SQLStatement.buildWhere0(localkeys); 
        if (ast.isOneMany())
        { return new SQLStatement("DELETE",ents2,
                       localkeys,new ArrayList(),""); 
        }  // same as deleteentity2 by key. 
        else if (ast.isZeroOneMany())
        { ArrayList nulls = new ArrayList(); 
          nulls.add("NULL"); 
          ArrayList forkeys = new ArrayList(); 
          forkeys.add(akey); 
          return new SQLStatement("UPDATE",ents2,
                                  forkeys,nulls,wre); 
        }  
      }
    } 
    System.out.println("!! ERROR: Unknown action: " + action); 
    return null;
  }  
 
  public String getServletCode()
  { String res = "";
    String sname = getODName();
    String dbiop = getDbiOpCall();
    String action = getStereotype(0); 

    String ename = entity.getName(); 
    res = "import java.io.*;\n" +
          "import java.util.*;\n" +
          "import javax.servlet.http.*;\n" +
          "import javax.servlet.*;\n";
    if (action.equals("list") || action.equals("check") ||
        action.equals("get") || action.equals("searchBy")) 
    { res = res + "import java.sql.*;\n"; }

    res = res + "\n"; 

    res = res + 
      "public class " + sname +
      "Servlet extends HttpServlet\n"; 
    res = res + "{ private Dbi dbi; \n\n" +
          "  public " + sname + "Servlet() {}\n\n";

    res = res + "  public void init(ServletConfig cfg)\n" +
          "  throws ServletException\n" +
          "  { super.init(cfg);\n" +  
          "    dbi = new Dbi();\n" + 
          "  }\n\n"; 
    res = res + 
      "  public void doGet(HttpServletRequest req,\n" +
      "              HttpServletResponse res)\n" + 
      "  throws ServletException, IOException\n" +
      "  { res.setContentType(\"text/html\");\n" +
      "    PrintWriter pw = res.getWriter();\n" +
      "    ErrorPage errorPage = new ErrorPage();\n";

    Vector pars = getParameters();
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      String extractatt = att.extractCode();
      String testatt = att.getServletCheckCode();
      res = res + extractatt + testatt;
    }

    if (action.equals("create") || action.equals("edit") || action.equals("set"))
    { Vector tests = entity.getInvariantCheckTests(pars);
      System.out.println("Entity inv checks: " + tests);
      for (int k = 0; k < tests.size(); k++) 
      { String test = (String) tests.get(k); 
        res = res + 
          "    if (" + test + ") { }\n" + 
          "    else \n" + 
          "    { errorPage.addMessage(\"Constraint : " + test + " failed\"); }\n";
      }
    }   // for setatt, generate update code

    String code; 
    if (action.equals("create") || action.equals("delete") ||
        action.equals("add") || action.equals("set") ||
        action.equals("remove") || action.equals("edit"))
    { code = dbiop + "\n" +
      "      CommandPage cp = new CommandPage();\n" +
      "      pw.println(cp);\n"; 
    } 
    else // get, list, check   
    { if (action.equals("get"))
      { String role = getStereotype(1); 
        Association ast = entity.getRole(role); 
        if (ast == null) { return res; } 
        Entity entity2 = ast.getEntity2();
        ename = entity2.getName(); 
      } 
      String resultPage = ename.toLowerCase() + "resultpage"; 
      code = "ResultSet resultSet = " + dbiop + "\n" + 
      "       " + ename + "ResultPage " + resultPage + 
                                  " = new " + ename + "ResultPage();\n" + 
      "       while (resultSet.next())\n" + 
      "       { " + resultPage + ".addRow(resultSet); }\n" + 
      "       pw.println(" + resultPage + ");\n" + 
      "       resultSet.close();\n"; 
    }
    res = res + 
      "    if (errorPage.hasError())\n" +
      "    { pw.println(errorPage); }\n" +
      "    else \n" +
      "    try { " + code + 
      "    } catch (Exception e) \n" + 
      "    { e.printStackTrace(); \n" + 
      "      errorPage.addMessage(\"Database error\"); \n" + 
      "      pw.println(errorPage); }\n" + 
      "    pw.close();\n" + 
      "  }\n\n";

    res = res +
      "  public void doPost(HttpServletRequest req,\n" + 
      "               HttpServletResponse res)\n" +
      "  throws ServletException, IOException\n" +
      "  { doGet(req,res); }\n\n"; 

    res = res + 
      "  public void destroy()\n" +
      "  { dbi.logoff(); }\n" +
      "}\n";
    return res;
  }

  public String jspUpdateDeclarations(String ename, String appName)
  { String bean = ename.toLowerCase();
    String beanclass = appName + "." + ename + "Bean";
    return "<jsp:useBean id=\"" + bean +
           "\" scope=\"request\" \n " + 
           "class=\"" + beanclass + "\"/>";
  }

  public String jspParamTransfers(String ename, Vector atts)
  { String bean = ename.toLowerCase();
    String res = "";
    for (int i = 0; i < atts.size(); i++)
    { Attribute att = (Attribute) atts.get(i);
      String nme = att.getName();
      res = res +
        "<jsp:setProperty name=\"" + bean +
        "\"  property=\"" + nme + 
        "\"  param=\"" + nme + "\"/>\n\r";
    }
    return res;
  }

  
  public String jspUpdateText(String op, String ename, Vector atts, String appName)
  { String bean = ename.toLowerCase();
    String dec = jspUpdateDeclarations(ename,appName);
    String sets = jspParamTransfers(ename, atts);
    String res = dec + "\n\r" + sets + "\n\r" +
      "<html>\n\r" +
      "<head><title>" + op + "</title></head>\n\r" +
      "<body>\n\r" +
      "<h1>" + op + "</h1>\n\r" +
      "<% if (" + bean + ".is" + op + "error())\n\r" +
      "{ %> <h2>Error in data: <%= " + bean +
      ".errors() %></h2>\n\r" +
      "<h2>Press Back to re-enter</h2> <% }\n\r" +
      "else { " + bean + "." + op + "(); %>\n\r" +
      "<h2>" + op + " performed</h2>\n\r" +
      "<% } %>\n\r\n\r" +
      "<hr>\n\r\n\r" +
      "<%@ include file=\"index.html\" %>\n\r" +
      "</body>\n\r</html>\n\r";
    return res;
  }

  public String jspQueryDeclarations(String ename, String appName)
  { String bean = ename.toLowerCase();
    String beanclass = appName + "." + ename + "Bean";
    String res = "<%@ page import = \"java.util.*\" %>\n\r" +
      "<%@ page import = \"" + appName + ".*\" %>\n\r" +
      "<jsp:useBean id=\"" + bean +
           "\" scope=\"request\" \n\r " + 
           "class=\"" + beanclass + "\"/>";
      return res;
  }

  public String jspQueryText(String op,
                             String ename, Vector atts, Entity ent, String appName)
  { String bean = ename.toLowerCase();
    String dec = jspQueryDeclarations(ename,appName);
    String sets = jspParamTransfers(ename, atts);
    Entity ent2 = ent; 
    String action = getStereotype(0); 
    if (action.equals("get"))
    { String role = getStereotype(1); 
      Association ast = ent.getRole(role); 
      if (ast != null)
      { ent2 = ast.getEntity2(); }
    }
    String e2name = ent2.getName(); 
    String e2bean = e2name.toLowerCase(); 

    String res = dec + "\n\r" + sets + "\n\r" +
      "<html>\n\r" +
      "<head><title>" + op + " results</title></head>\n\r" +
      "<body>\n\r" +
      "<h1>" + op + " results</h1>\n\r" +
      "<% Iterator " + bean + "s = " + bean + "." + op +
      "(); %>\n\r" +
      "<table border=\"1\">\n\r" +
      ent2.getTableHeader() + "\n\r" +
      "<% while (" + bean + "s.hasNext())\n\r" +
      "{ " + e2name + "VO " + e2bean + "VO = (" + 
      e2name + "VO) " + bean + "s.next(); %>\n\r" +
      ent2.getTableRow() + "\n\r" +
      "<% } %>\n\r</table>\n\r\n\r<hr>\n\r\n\r" +
      "<%@ include file=\"index.html\" %>\n\r" +
      "</body>\n\r</html>\n\r";
    return res;
  }

  public String getJsp(String appName)
  { String action = getODName();
    String op = getStereotype(0);
    String ename = entity.getName();
    Vector pars = getParameters();
    if (op.equals("create") || op.equals("delete") ||
        op.equals("edit") || op.equals("add") ||
        op.equals("set") || op.equals("remove"))
    { return jspUpdateText(action,ename,pars,appName); }
    return jspQueryText(action,ename,pars,entity,appName);
  }

  public String getRESTJsp(String appname)
  { String action = getName();
    String op = getStereotype(0);
    String ename = entity.getName();
    Vector pars = getParameters();
    if (op.equals("create") || op.equals("delete") ||
        op.equals("edit") || op.equals("add") ||
        op.equals("set") || op.equals("remove"))
    { return jspRESTText(action,pars); }  
    return jspRESTQueryText(action,ename,pars,entity);
  }

  public String jspRESTQueryText(String op,
                             String ename, Vector atts, Entity ent)
  { String bean = ename.toLowerCase();
    String dec = jspRESTDeclarations();
    String sets = jspRESTParamTransfers(atts);
    Entity ent2 = ent; 
    String action = getStereotype(0); 
    if (action.equals("get"))
    { String role = getStereotype(1); 
      Association ast = ent.getRole(role); 
      if (ast != null)
      { ent2 = ast.getEntity2(); }
    }
    String e2name = ent2.getName(); 
    String e2bean = e2name.toLowerCase(); 

    String res = dec + "\n\r" + sets + "\n\r" +
      "<html>\n\r" +
      "<head><title>" + op + " results</title></head>\n\r" +
      "<body>\n\r" +
      "<h1>" + op + " results</h1>\n\r" +
      "<% List " + bean + "s = " + bean + "." + op + "(); %>\n\r" +
      "<table border=\"1\">\n\r" +
      "<tr><th>" + ename + "</th></tr>\n\r" +
      "<% for (int i = 0; i < " + bean + "s.size(); i++)\n\r" +
      "{ %> <tr><td><%=" + bean + "s.get(i)%></td></tr>\n" +
      "<% } %>\n\r" + 
	  "</table>\n\r<hr>\n\r\n\r" +
      "<%@ include file=\"commands.html\" %>\n\r" +
      "</body>\n\r</html>\n\r";
    return res;
  }

  public String jspRESTText(String op,
                              Vector atts)
  { String bean = "bean";
    String dec = jspRESTDeclarations();
    String sets = jspRESTParamTransfers(atts);
    String showresult = ""; 

    if (op.startsWith("list") || op.startsWith("searchBy")) 
    { // String op1 = (op.charAt(0) + "").toUpperCase() + op.substring(1,op.length()); 
      showresult = "<p><strong> Result = </strong> <%= " + bean + "." + op + "()%> </p>\n\r"; 
    } 
    String res = dec + "\n\r" + 
      sets + "\n\r" +
      "<html>\n\r" +
      "<head><title>" + op + "</title></head>\n\r" +
      "<body>\n\r" +
      "<h1>" + op + "</h1>\n\r" +
      "<% if (" + bean + ".is" + op + "error())\n\r" +
      "{ %> <h2>Error in data: <%= " + bean + "." + op + "errors() %></h2>\n\r" +
      "<h2>Press Back to re-enter</h2> <% }\n\r"; 

    if (op.startsWith("list") || op.startsWith("searchBy"))
    { res = res + "else { %> " + showresult + "\n\r" +
        "<h2>" + op + " performed</h2> <% } %>\n\r"; 
    } 
    else 
    { res = res + "else { %> " + bean + "." + op + "();\n\r" +
        "<h2>" + op + " performed</h2> <% } %>\n\r" +  
        "\n\r\n\r"; 
    }
    res = res +  
      "<hr>\n\r\n\r" +
      "</body>\n\r</html>\n\r";
    return res;
  }

  public String jspRESTDeclarations()
  { String nme = getName();
    String beanclass = "beans.ControllerBean";
    return "<jsp:useBean id=\"bean\" scope=\"session\" \n " + 
           "class=\"" + beanclass + "\"/>";
  }


  public String jspRESTParamTransfers(Vector atts)
  { String bean = "bean";
    String ucname = getName(); 
    String res = "";
    for (int i = 0; i < atts.size(); i++)
    { Attribute att = (Attribute) atts.get(i);
      String nme = att.getName();
      res = res +
        "<jsp:setProperty name=\"" + bean +
        "\"  property=\"" + ucname + "_" + nme + 
        "\"  param=\"" + nme + "\"/>\n\r";
    }
    return res;
  }


  public String getInputPage(String appName)
  { String codebase = "http://127.0.0.1:8080/examples/jsp/" + appName + "/";
    String op = getODName();
    String action = getStereotype(0);
    String jsp = codebase + op + ".jsp";
    String method = "GET";
    if (action.equals("create") || action.equals("delete") ||
        action.equals("edit") || 
        action.equals("add") || action.equals("set") ||
        action.equals("remove"))
    { method = "POST"; }
    String res = "<html>\n\r" +
      "<head><title>" + op + " form</title></head>\n\r" +
      "<body>\n\r" +
      "<h1>" + op + " form</h1>\n\r" +
      "<form action = \"" + jsp + "\" method = \"" +
      method + "\" >\n\r";
    Vector pars = getParameters();
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      res = res + att.getFormInput() + "\n\r";
    }
    res = res + "<input type=\"submit\" value = \"" + 
          op + "\"/>\n\r</form>\n\r</body>\n\r</html>";
    return res;
  }

 /*  public String getInputPage(String appname)
  { String codebase = "http://127.0.0.1:8080/" + appname + "/servlets/";
    String op = getODName();
    String action = getStereotype(0);
    String jsp = codebase + op + ".jsp";
    String method = "GET";
    if (action.equals("create") || action.equals("delete") ||
        action.equals("edit") || 
        action.equals("add") || action.equals("set") ||
        action.equals("remove"))
    { method = "POST"; }
    String res = "<html>\n\r" +
      "<head><title>" + op + " form</title></head>\n" +
      "<body>\n" +
      "<h1>" + op + " form</h1>\n" +
      "<form action = \"" + jsp + "\" method = \"" +
      method + "\" >\n\r";
    Vector pars = getParameters();
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      res = res + att.getFormInput() + "\n";
    }
    res = res + "<input type=\"submit\" value = \"" + 
          op + "\"/>\n\r</form>\n\r</body>\n\r</html>";
    return res;
  } */ 

  public String getGenerationClass()
  { String nme = getName();
    String codebase = "http://localhost:8080/servlet/"; 
    String ename = entity.getName();
    String action = getStereotype(0);
    String rolename = "";
    if (getStereotype(1) != null)
    { rolename = getStereotype(1); } 
    String servlet = codebase + getODName() + "Servlet";
    String res = "public class " + nme + 
      "Page extends BasePage\n" +
      "{ protected HtmlForm form = new HtmlForm();\n" +
      "  protected HtmlInput button = new HtmlInput();\n\n" +
      "  public " + nme + "Page()\n" +
      "  { super();\n" +
      "    HtmlText heading = new HtmlText(\"" +
      action + " " + ename + rolename + " form\",\"h1\");\n" +
      "    body.add(0,heading);\n" +
      "    form.setAttribute(\"action\",\"" + 
      servlet + "\");\n" + 
      "    HtmlItem para = new HtmlItem(\"p\");\n" + 
      "    form.setAttribute(\"method\",\"POST\");\n" +
      "    button.setAttribute(\"type\",\"submit\");\n" +
      "    button.setAttribute(\"value\",\"" + 
      action + "\");\n" +
      "    body.add(form);\n";
    Vector pars = getParameters();
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      String attItem = att.getHtmlGen();
      res = res + attItem;
    }
    res = res + "    form.add(button);\n" +
          "  }\n" + "}\n";
    return res;
  } // use GET for query ops

  public String getDbiOpCode()
  { String action = getStereotype(0);
    String ename = entity.getName();
    Vector pars = getParameters();
    String stat = getODName() + "Statement";
    String res = "{ try\n" +
      "    { ";
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      Type t = att.getType();
      String nme = att.getName();
      if ("int".equals(t.getName()) || "long".equals(t.getName()))
      { res = res + "  " + stat + ".setInt(" + (i+1) + ", " +
                            nme + ");\n    "; 
      }
      else if ("double".equals(t.getName()))
      { res = res + "  " + stat + ".setDouble(" + (i+1) + ", " +
                               nme + ");\n    "; 
      }
      else 
      { res = res + "  " + stat + ".setString(" + (i+1) + ", " +
                               nme + ");\n    "; 
      }
    }

    if (action.equals("get") || 
        action.equals("list") || action.equals("check") ||
        action.equals("searchBy"))
    { res = res + "  return " + stat + ".executeQuery();\n" +
            "  } catch (Exception e) { e.printStackTrace(); }\n" +
            "  return null; }\n";
    }
    else 
    { res = res + "  " + stat + ".executeUpdate();\n" +
        "    connection.commit();\n" +
        "  } catch (Exception e) { e.printStackTrace(); }\n}\n";
    }
    return res;
  }

  public String getJSPDbiOpCode()
  { String action = getStereotype(0);
    String ename = entity.getName();
    Vector pars = new Vector(); 
    String stat = getODName() + "Statement";
    String res = "  try\n" +
      "    { ";

    if ("remove".equals(action)) 
    { String role = getStereotype(1); 
      Association ast = entity.getRole(role); 
      if (ast != null && 
          ast.isZeroOneMany())
      { // Only 2nd parameter counts.
        Vector allpars = getParameters(); 
        pars.add(allpars.get(1)); 
      } 
      else 
      { pars.addAll(getParameters()); } 
    } 
    else
    { pars.addAll(getParameters()); } 
 
    for (int i = 0; i < pars.size(); i++)
    { Attribute att = (Attribute) pars.get(i);
      Type t = att.getType();
      String nme = att.getName();
      if ("int".equals(t.getName()) || "long".equals(t.getName()))
      { res = res + "  " + stat + ".setInt(" + (i+1) + ", " +
                            nme + ");\n    "; 
      }
      else if ("double".equals(t.getName()))
      { res = res + "  " + stat + ".setDouble(" + (i+1) + ", " +
                               nme + ");\n    "; 
      }
      else 
      { res = res + "  " + stat + ".setString(" + (i+1) + ", " +
                               nme + ");\n    "; 
      }
    }

    if (action.equals("get") || 
        action.equals("list") || action.equals("check") ||
        action.equals("searchBy"))
    { res = res + "  return " + stat + ".executeQuery();\n" +
            "  } catch (Exception e) { e.printStackTrace(); }\n" +
            "  return null; }\n";
    }
    else 
    { res = res + "  " + stat + ".executeUpdate();\n" +
        "    connection.commit();\n" +
        "  } catch (Exception e) { e.printStackTrace(); }\n}\n";
    }
    return res;
  }

  // for REST web service: 
  public static void createControllerBean(Vector usecases, Vector entities, PrintWriter out)
  { out.println("package beans;\n\n");
    out.println("import java.util.*;\n");
    out.println("\n");
    out.println("public class ControllerBean");
    out.println("{ Controller cont;");
    out.println("");
    out.println("  public ControllerBean() { cont = Controller.inst(); }");
    out.println("");
    for (int i = 0; i < usecases.size(); i++)
    { Object obj = usecases.get(i);
      if (obj instanceof UseCase)
      { UseCase uc = (UseCase) obj;
        uc.generateControllerBeanAttributes(out);
      } // parameters must all have different names, only one result. 
      else if (obj instanceof OperationDescription)
      { OperationDescription od = (OperationDescription) obj; 
        od.generateControllerBeanAttributes(out); 
      }
    }
    out.println("\n");
    for (int i = 0; i < usecases.size(); i++)
    { Object obj = usecases.get(i);
      if (obj instanceof UseCase)
      { UseCase uc = (UseCase) obj;
        uc.generateControllerBeanOps(out);
      }
      else if (obj instanceof OperationDescription)
      { OperationDescription od = (OperationDescription) obj; 
        od.generateControllerBeanOps(out); 
      }
    }
    out.println("}\n");
  }
  
   public void generateControllerBeanAttributes(PrintWriter out)
   { String nme = getName();
     
     Vector pars = getParameters(); 
	 
     for (int i = 0; i < pars.size(); i++)
     { Attribute att = (Attribute) pars.get(i);
       String par = att.getName();
       Type typ = att.getType();         
       String tname = typ.getJava();
       out.println("  " + tname + " " + nme + "_" + par + ";\n");
     } 

     if (nme.startsWith("list") || nme.startsWith("searchBy"))
     { 
       out.println("  List<String> " + nme + "result = new ArrayList<String>();\n");
     }

     out.println("  List<String> " + nme + "errors = new ArrayList<String>();\n\r");
      
    }  
 

   public void generateControllerBeanOps(PrintWriter out)
   { // set and get ops for attributes,
     // and the op itself

      Entity ent = getEntity(); 
      String ename = ent.getName(); 
      String nme = getName();

      String opcallpars = "";
      String updateEnt = ""; 
      String errorlist = nme + "errors"; 
      Vector pars = getParameters(); 

      for (int i = 0; i < pars.size(); i++)
      { Attribute att = (Attribute) pars.get(i);
        String par = att.getName();
        Type typ = att.getType();         
        String tname = typ.getJava();
        String parname = nme + "_" + par;
		
        opcallpars = opcallpars + parname;
        updateEnt = updateEnt + "    cont.set" + par + "(_obj," + parname + ");\n"; 
        if (i < pars.size()-1)
        { opcallpars = opcallpars + ","; }

        out.println("  public void set" + parname + "(String _s)");
        out.println("  {"); 
        String convertcode = "    " + parname + " = _s;";  
        if ("String".equals(tname)) {}
        else if ("int".equals(tname))
        { convertcode = "    try { " + parname + " = Integer.parseInt(_s); }\n" + 
            "    catch (Exception _e)\n" + 
            "    { " + errorlist + ".add(\"" + par + " is not an int\");\n" +  
            "      return; }";
        }
        else if ("long".equals(tname))
        { convertcode = "    try { " + parname + " = Long.parseLong(_s); }\n" + 
		                "    catch (Exception _e)\n" + 
						"    { " + errorlist + ".add(\"" + par + " is not a long\");\n" +
						"      return; }";
        }
        else if ("double".equals(tname))
        { convertcode = "    try { " + parname + " = Double.parseDouble(_s); }\n" + 
		                "    catch (Exception _e)\n" + 
						"    { " + errorlist + ".add(\"" + par + " is not a double\");\n" +
						"      return; }";
        }
        else if ("boolean".equals(tname))
        { convertcode = "    if (\"true\".equals(_s) { " + parname + " = true; } else { " + parname + " = false; }"; 
        }
        else if (typ.isEntity())
        { convertcode = "    " + parname + " = cont.get" + tname + "ByPK(_s);"; } 
 
        out.println(convertcode);
        out.println("  }\n\n");
      }


      if (nme.startsWith("list"))
      { // String rtyp = resultType.getJava();
        out.println("  public List<String> " + nme + "()");
        out.println("  { List all" + ename + " = cont." + ename.toLowerCase() + "s;"); 
        out.println("    " + nme + "result.clear();"); 
        out.println("    for (int _i = 0; _i < all" + ename + ".size(); _i++)"); 
        out.println("    { " + nme + "result.add(\"\" + all" + ename + ".get(_i)); }");
        out.println("");  
        out.println("    return " + nme + "result;"); 
        out.println("  }\n\n");

        out.println("  public List<String> get" + nme + "Result() { return " + nme + "result; }\n");
      } 
      else if (nme.startsWith("searchBy"))
      { // String rtyp = resultType.getJava();
	    Attribute par1 = (Attribute) pars.get(0); 
		String par1name = par1.getName(); 
		
        out.println("  public List<String> " + nme + "()");
        out.println("  { List all" + ename + " = cont." + ename.toLowerCase() + "s;"); 
        out.println("    " + nme + "result.clear();"); 
        out.println("    for (int _i = 0; _i < all" + ename + ".size(); _i++)"); 
        out.println("    { " + ename + " _obj = " + ename + ".get(_i);"); 
		out.println("      if ((_obj." + par1name + " + \"\").equals(\"\" + " + par1name + "))"); 
		out.println("      { " + nme + "result.add(\"\" + _obj); }");
		out.println("    }"); 
        out.println("");  
        out.println("    return " + nme + "result;"); 
        out.println("  }\n\n");

        out.println("  public List<String> get" + nme + "Result() { return " + nme + "result; }\n");
      } 
      else if (nme.startsWith("create"))
      { Attribute pk = ent.getPrincipalPrimaryKey(); 
        String key = pk.getName(); 
        out.println("  public void " + nme + "()");
        out.println("  { " + ename + " _obj = cont.get" + ename + "ByPK(" + nme + "_" + key + ");"); 
        out.println("    if (_obj == null)");  
        out.println("    { _obj = cont.create" + ename + "(); }");  
        out.println(updateEnt); 
        out.println("  }");  
        out.println("\n\n"); 
      } 
      else if (nme.startsWith("edit"))
      { Attribute pk = ent.getPrincipalPrimaryKey(); 
        String key = pk.getName(); 
        out.println("  public void " + nme + "()");
        out.println("  { " + ename + " _obj = cont.get" + ename + "ByPK(" + nme + "_" + key + ");"); 
        out.println("    if (_obj == null)");
        out.println("    { _obj = cont.create" + ename + "(); }");  
        out.println(updateEnt); 
        out.println("  }"); 
        out.println("\n\n"); 
      } 
      else if (nme.startsWith("delete"))
      { Attribute pk = ent.getPrincipalPrimaryKey(); 
        String key = pk.getName(); 
        out.println("  public void " + nme + "()");
        out.println("  { " + ename + " _obj = cont.get" + ename + "ByPK(" + nme + "_" + key + ");"); 
        out.println("    if (_obj != null)");
        out.println("    { cont.kill" + ename + "(_obj); }");  
        out.println("  }"); 
        out.println("\n\n"); 
      } 
      
      out.println("  public boolean is" + nme + "error() { return " + nme + "errors.size() > 0; }\n");
      out.println("  public List<String> " + nme + "errors() { return " + nme + "errors; }\n");
    } 

   public void generateWebServiceOp(PrintWriter out)
   { // set and get ops for attributes,
     // and the op itself

      Entity ent = getEntity(); 
      String ename = ent.getName(); 
      String nme = getName();

      String opcallpars = "";
      String updateEnt = ""; 
      String errorlist = nme + "errors"; 
      Vector pars = getParameters(); 

      for (int i = 0; i < pars.size(); i++)
      { Attribute att = (Attribute) pars.get(i);
        String par = att.getName();
        Type typ = att.getType();         
        String tname = typ.getJava7();
        String parname = tname + " " + par;
		
        opcallpars = opcallpars + parname;
        updateEnt = updateEnt + "    cont.set" + par + "(_obj," + par + ");\n"; 
        if (i < pars.size()-1)
        { opcallpars = opcallpars + ","; }
      } 

      if (nme.startsWith("list"))
      { // String rtyp = resultType.getJava();
        out.println("  @WebMethod( operationName = \"" + nme + "\" )");
        out.println("  public List<String> " + nme + "()");
        out.println("  { List all" + ename + " = cont." + ename.toLowerCase() + "s;"); 
        out.println("    List " + nme + "result = new ArrayList<String>();"); 
        out.println("    for (int _i = 0; _i < all" + ename + ".size(); _i++)"); 
        out.println("    { " + nme + "result.add(\"\" + all" + ename + ".get(_i)); }");
        out.println("");  
        out.println("    return " + nme + "result;"); 
        out.println("  }\n\n");
      } 
      else if (nme.startsWith("searchBy"))
      { // String rtyp = resultType.getJava();
	    Attribute par1 = (Attribute) pars.get(0); 
		String par1name = par1.getName(); 
		
        out.println("  @WebMethod( operationName = \"" + nme + "\" )");
        out.println("  public List<String> " + nme + "(" + opcallpars + ")");
        out.println("  { List all" + ename + " = cont." + ename.toLowerCase() + "s;"); 
        out.println("    List " + nme + "result = new ArrayList<String>();"); 
        out.println("    for (int _i = 0; _i < all" + ename + ".size(); _i++)"); 
        out.println("    { " + ename + " _obj = " + ename + ".get(_i);"); 
		out.println("      if ((_obj." + par1name + " + \"\").equals(\"\" + " + par1name + "))"); 
		out.println("      { " + nme + "result.add(\"\" + _obj); }");
		out.println("    }"); 
        out.println("");  
        out.println("    return " + nme + "result;"); 
        out.println("  }\n\n");
      } 
      else if (nme.startsWith("create"))
      { Attribute pk = ent.getPrincipalPrimaryKey(); 
        String key = pk.getName(); 
        out.println("  @WebMethod( operationName = \"" + nme + "\" )");
        out.println("  public void " + nme + "(" + opcallpars + ")");
        out.println("  { " + ename + " _obj = cont.get" + ename + "ByPK(" + key + ");"); 
        out.println("    if (_obj == null)");  
        out.println("    { _obj = cont.create" + ename + "(); }");  
        out.println(updateEnt); 
        out.println("  }");  
        out.println("\n\n"); 
      } 
      else if (nme.startsWith("edit"))
      { Attribute pk = ent.getPrincipalPrimaryKey(); 
        String key = pk.getName(); 
        out.println("  @WebMethod( operationName = \"" + nme + "\" )");
        out.println("  public void " + nme + "(" + opcallpars + ")");
        out.println("  { " + ename + " _obj = cont.get" + ename + "ByPK(" + key + ");"); 
        out.println("    if (_obj == null)");
        out.println("    { _obj = cont.create" + ename + "(); }");  
        out.println(updateEnt); 
        out.println("  }"); 
        out.println("\n\n"); 
      } 
      else if (nme.startsWith("delete"))
      { Attribute pk = ent.getPrincipalPrimaryKey(); 
        String key = pk.getName(); 
        out.println("  @WebMethod( operationName = \"" + nme + "\" )");
        out.println("  public void " + nme + "(" + opcallpars + ")");
        out.println("  { " + ename + " _obj = cont.get" + ename + "ByPK(" + key + ");"); 
        out.println("    if (_obj != null)");
        out.println("    { cont.kill" + ename + "(_obj); }");  
        out.println("  }"); 
        out.println("\n\n"); 
      } 
    } 


  // for SOAP web service: 
  public static void createWebServiceBean(Vector usecases, Vector entities, PrintWriter out)
  { out.println("package beans;"); 
    out.println(); 
    out.println("import java.util.*;\n\r");
    out.println("import javax.jws.WebService;\n\r");
    out.println("import javax.jws.WebMethod;\n\r");
    out.println("import javax.jws.WebParam;\n\r\n\r");
    out.println("@WebService( name = \"ControllerWebBean\",  serviceName = \"ControllerWebBeanService\" )");
    out.println("public class ControllerWebBean");
    out.println("{ Controller cont;");
    out.println("");
    out.println("  public ControllerWebBean() { cont = Controller.inst(); }");
    out.println("");
    for (int i = 0; i < usecases.size(); i++)
    { Object obj = usecases.get(i);
      if (obj instanceof UseCase)
      { UseCase uc = (UseCase) obj;
        uc.generateWebServiceOp(out);
      }
      else if (obj instanceof OperationDescription)
      { OperationDescription od = (OperationDescription) obj;
        od.generateWebServiceOp(out);
      }
    }
    out.println("");
    out.println("}\n");
  }

public void iOSViewController(String systemName, PrintWriter out)
{ String op = getAction();
  
  IOSAppGenerator gen = new IOSAppGenerator(); 
  
  if (op.startsWith("create"))
  { gen.createViewController(systemName,entity,out); }
  else if (op.startsWith("delete"))
  { gen.deleteViewController(systemName,entity,out); }
  else if (op.startsWith("edit"))
  { gen.editViewController(systemName,entity,out); }
  else if (op.startsWith("list"))
  { gen.listViewController(entity,out); }
  else if (op.startsWith("searchBy"))
  { Attribute byatt = entity.getAttribute(oprole); 
    gen.searchByViewController(systemName,entity,byatt,out); 
  } 
  else if (op.startsWith("add"))
  { gen.addViewController(systemName,entity,oprole,out); } 
  else if (op.startsWith("remove"))
  { gen.removeViewController(systemName,entity,oprole,out); } 
  else 
  { System.err.println("!! No iOS screen is defined yet for " + op); } 
}

  public void generateViewController(PrintWriter out)
  { // for createE, editE, deleteE

    String op = getAction(); 
    String ename = entity.getName(); 
    String ucname = op + ename;
    String evc = ucname + "ViewController";
    // String evo = ename + "ValueObject";
    // String vo = evo.toLowerCase();
    String resvo = "result";
    String restype = ""; 
    String ebean =  "ModelFacade";
    String bean = ebean.toLowerCase();
    Vector atts = getParameters();
    // Attribute res = uc.getResultParameter(); 

    // String evocreate = createVOStatement(e,atts);

    out.println("import UIKit");
    out.println();
    out.println("class " + evc + " : UIViewController");
    out.println("{");
    out.println("  var " + bean + " : " + ebean + " = " + ebean + ".getInstance()");

    String parlist = ""; 
    for (int x = 0; x < atts.size(); x++)
    { Attribute att = (Attribute) atts.get(x);
      // if (att.isInputAttribute())
      out.println("  @IBOutlet weak var " + att + "Input: UITextField!");
      
      parlist = parlist + att.getName(); 
      if (x < atts.size() - 1) 
      { parlist = parlist + ", "; } 
    } 
    
    /* if (res != null)
    { out.println("  @IBOutlet weak var resultOutput: UILabel!");
      restype = res.getType().getSwift(); 
    } */ 

    out.println("  var userId : String = " + "\"0\"");
    out.println();
    out.println("  override func viewDidLoad()");
    out.println("  { super.viewDidLoad()");
    // out.println("    self." + elist + " = " + bean + "." + getlist + "()");
    out.println("  }");
    out.println("");
 
    String attdecoder = "    guard ";
    boolean previous = false;
    String localVars = "";

    for (int x = 0; x < atts.size(); x++)
    { Attribute att = (Attribute) atts.get(x);
      // if (att.isInputAttribute())
      { if (previous)
        { attdecoder = attdecoder + ", "; }
        attdecoder = attdecoder + " let " + att + " = " + 
                     Expression.unwrapSwift(att + "Input.text",att.getType());
        previous = true;
      }
      // else 
      // { Type atype = att.getType(); 
	 //  if (atype != null) 
      //  { localVars = localVars + "    var " + att + " : " + atype.getSwift() + "\n"; }
      // }  
    }
    attdecoder = attdecoder + " else { return }\n";

    String updateScreen = "";
   
    /* if (res != null) 
    { Attribute att = res; // (Attribute) atts.get(x);
      // if (att.isOutput())
      { updateScreen = updateScreen + "    " + att + "Output.text = String(" + att + ")";
      }
    } */ 

      out.println("  @IBAction func " + ucname + "(_ sender: Any) {");
      if (atts.size() > 0) 
      { out.println(attdecoder); } 
      // out.println(localVars);
      // out.println(evocreate);
      /* if (res != null) 
      { out.println("    var " + resvo + " : " + restype + " = " + bean + "." + ucname + "(" + parlist + ")"); 
        out.println(updateScreen);
      } 
      else */  
      { out.println("    " + bean + "." + ucname + "(" + parlist + ")"); } 
      out.println("  }");
    // }

    out.println("");
 
    out.println("  override func didReceiveMemoryWarning()");
    out.println("  { super.didReceiveMemoryWarning() }");
    out.println("");
    out.println("}");
  } 


public void androidViewActivity(String packageName, PrintWriter out)
{ String op = getAction();
  AndroidAppGenerator gen = new AndroidAppGenerator(); 
    
  if (op.startsWith("create"))
  { androidCreateViewActivity(op,packageName,out); }
  else if (op.startsWith("delete"))
  { gen.androidDeleteViewActivity(op,entity,out); }
  else if (op.startsWith("edit"))
  { gen.androidEditViewActivity(op,entity,out); }
  else if (op.startsWith("list"))
  { gen.listViewController(entity,out); }
  else if (op.startsWith("searchBy"))
  { androidSearchByViewActivity(op,packageName,out); }
}

public void androidCreateViewActivity(String op, String systemName, PrintWriter out)
{ Entity ent = getEntity();
  String entname = ent.getName();
  String fullop = op + entname; 
  String beanclass = entname + "Bean";
  String bean = beanclass.toLowerCase();
  String lcname = fullop.toLowerCase(); 

  out.println("package " + systemName + ";\n"); 
  out.println(); 
  out.println("import androidx.appcompat.app.AppCompatActivity;\n\r");
  out.println("import android.os.Bundle;");
  out.println("// import android.app.Activity;");
  out.println("import android.view.View;");
  out.println("import android.util.Log;"); 
  out.println("import android.widget.Toast;");
  out.println("import android.widget.TextView;");
  out.println("import android.widget.EditText;\n\r");
  out.println(); 

  out.println("public class View" + fullop + " extends AppCompatActivity");
  out.println("{ " + beanclass + " " + bean + ";");

  Vector pars = getParameters();
  for (int x = 0; x < pars.size(); x++)
  { Attribute par = (Attribute) pars.get(x);
    String pnme = par.getName(); 
    String tfnme = pnme + "TextField"; 
    String dname = pnme + "Data";
    out.println("  EditText " + tfnme + ";");
    out.println("  String " + dname + " = \"\";");
  }
  out.println();
  out.println();
  out.println("  @Override");
  out.println("  protected void onCreate(Bundle bundle)");
  out.println("  { super.onCreate(bundle);");
  out.println("    setContentView(R.layout." + lcname + "_layout);");

  for (int x = 0; x < pars.size(); x++)
  { Attribute par = (Attribute) pars.get(x);
    String pnme = par.getName(); 
    String tfnme = pnme + "TextField"; 
    out.println("    " + tfnme + " = (EditText) findViewById(R.id." + fullop + pnme + ");");
  }
  out.println("    " + bean + " = new " + beanclass + "(this);");
  out.println("  }\n\r");
  out.println(); // for edit, the principal primary key does not have an EditText
  
  out.println("  public void " + fullop + "OK(View _v) ");
  out.println("  {");
  for (int x = 0; x < pars.size(); x++)
  { Attribute att = (Attribute) pars.get(x);
    String aname = att.getName();
    String tfnme = aname + "TextField"; 
    String dname = aname + "Data";
    out.println("    " + dname + " = " + tfnme + ".getText() + \"\";");
    out.println("    " + bean + ".set" + aname + "(" + dname + ");"); 
  }
  out.println("    if (" + bean + ".is" + fullop + "error())"); 
  out.println("    { Log.w(getClass().getName(), " + bean + ".errors()); }"); 
  out.println("    else"); 
  out.println("    { " + bean + "." + fullop + "(); }"); 

  out.println("  }\n\r");
  out.println();

  out.println("  public void " + fullop + "Cancel(View _v)");
  out.println("  { " + bean + ".resetData();");
  for (int x = 0; x < pars.size(); x++)
  { Attribute att = (Attribute) pars.get(x);
    String aname = att.getName();
    String tfnme = aname + "TextField"; 
    out.println("    " + tfnme + ".setText(\"\");");
	// if (res != null) 
	// { out.println("    " + op + "Result.setText(\"" + op + " result\");"); }
  }
  out.println("  }");    

  out.println("}"); 
}


public static void androidCreateMenu(Vector ops, PrintWriter out)
{ out.println("<menu xmlns:android=\"http://schemas.android.com/apk/res/android\"");
  out.println("  xmlns:app=\"http://schemas.android.com/apk/res-auto\"");
  out.println("  xmlns:tools=\"http://schemas.android.com/tools\"");
  out.println("  tools:context=\".MainActivity\" >");
  out.println();

  for (int x = 0; x < ops.size(); x++)
  { if (ops.get(x) instanceof OperationDescription) 
    { OperationDescription op = (OperationDescription) ops.get(x);
      op.androidTabItem(out);
    } 
    else if (ops.get(x) instanceof UseCase)
    { UseCase uc = (UseCase) ops.get(x); 
      if (uc.includedIn.size() == 0 && uc.extensionOf.size() == 0)
      { uc.androidTabItem(out); }  
    } 
  }

  out.println();
  out.println("</menu>");
}

public void androidTabItem(PrintWriter out)
{ String fullop = getName();
  String titleop = Named.capitalise(fullop);
  out.println("  <item android:id=\"@+id/" + fullop + "\"");
  out.println("    android:title=\"" + titleop + "\"");
  out.println("    android:showAsAction=\"always\" />");
}

public static void androidCreateMainActivity(Vector ops, PrintWriter out)
{ out.println("import android.os.Bundle;");
  out.println("import android.app.Activity;");
  out.println("import android.content.Intent;");
  out.println("import android.app.FragmentTransaction;");
  out.println("import android.view.Menu;");
  out.println("import android.view.MenuItem;");
  out.println("import android.app.ActionBar;");
  out.println("import android.app.ActionBar.Tab;");
  out.println("");
  out.println("public class MainActivity extends Activity implements ActionBar.TabListener");
  out.println("{ ");
  out.println();

  out.println("  @Override");
  out.println("  protected void onCreate(Bundle bundle)");
  out.println("  { super.onCreate(bundle);");
  out.println("    setContentView(R.layout.activity_main);");
  out.println("    ActionBar bar = this.getActionBar();");
  out.println("    bar.setTitle(\"My application\");"); 
  out.println("    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);");

  for (int x = 0; x < ops.size(); x++)
  { if (ops.get(x) instanceof OperationDescription)
    { OperationDescription op = (OperationDescription) ops.get(x);
      String pnme = op.getName(); 
      String tfnme = pnme + "Tab"; 
      String tabname = Named.capitalise(pnme); 
      out.println("    Tab " + tfnme + " = bar.newTab();");
      out.println("    " + tfnme + ".setText(\"" + tabname + "\");");
      out.println("    " + tfnme + ".setTabListener(this);");
      out.println("    bar.addTab(" + tfnme + ");");
    }
    else if (ops.get(x) instanceof UseCase)
    { UseCase op = (UseCase) ops.get(x);
      if (op.includedIn.size() == 0 && op.extensionOf.size() == 0)
	 { String pnme = op.getName(); 
        String tfnme = pnme + "Tab"; 
        String tabname = Named.capitalise(pnme); 
        out.println("    Tab " + tfnme + " = bar.newTab();");
        out.println("    " + tfnme + ".setText(\"" + tabname + "\");");
        out.println("    " + tfnme + ".setTabListener(this);");
        out.println("    bar.addTab(" + tfnme + ");");
      } 
    } 
  }
  out.println("  }");

  out.println(); 
  out.println("  @Override");
  out.println("  public void onTabSelected(Tab tab, FragmentTransaction frag) ");
  out.println("  {");
  for (int x = 0; x < ops.size(); x++)
  { if (ops.get(x) instanceof OperationDescription)
    { OperationDescription op = (OperationDescription) ops.get(x);
      String opname = op.getName();
      String tabname = Named.capitalise(opname); 
      out.println("    if (\"" + tabname + "\".equals(tab.getText())) ");
      out.println("    { Intent " + opname + "Intent = new Intent(this, View" + opname + ".class);");
      out.println("      startActivity(" + opname + "Intent);");
      out.println("      return; }");
    } 
    else if (ops.get(x) instanceof UseCase)
    { UseCase op = (UseCase) ops.get(x);
      if (op.extensionOf.size() == 0 && op.includedIn.size() == 0) 
      { String opname = op.getName();
        String capsname = Named.capitalise(opname); 
        out.println("    if (\"" + capsname + "\".equals(tab.getText())) ");
        out.println("    { Intent " + opname + "Intent = new Intent(this, " + opname + "Activity.class);");
        out.println("      startActivity(" + opname + "Intent);");
        out.println("      return; }");
      } 
    }
  }
  out.println("  }");
  out.println();
  out.println("  @Override");
  out.println("  public void onTabUnselected(Tab tab, FragmentTransaction frag) {}");
  out.println();
  out.println("  @Override");
  out.println("  public void onTabReselected(Tab tab, FragmentTransaction frag) { }");
  out.println("}"); 
}

/* 
public void androidListScreen(String op, PrintWriter out)
{ String nme = getName();
  String viewname = "View" + nme;
  out.println("<TableLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"");
  out.println("  xmlns:tools=\"http://schemas.android.com/tools\"");
  out.println("  android:layout_width=\"match_parent\"");
  out.println("  android:layout_height=\"wrap_content\"");
  out.println("  tools:context=\"." + viewname + "\" >");
  out.println();
  out.println("  <TableRow>");
  Entity ent = getEntity();

  Vector pars = ent.getAttributes();
  for (int x = 0; x < pars.size(); x++)
  { Attribute att = (Attribute) pars.get(x);
    String attnme = att.getName();
    out.print("  <TextView android:text=\"" + attnme + "\" />");
  }
  out.println("  </TableRow>");

  out.println("</TableLayout>");
} */ 

  public void androidSearchByViewActivity(String op, String systemName, PrintWriter out)
  { String ename = entity.getName();
    String evc = "ViewsearchBy" + ename + oprole;
    String evo = ename + "VO";
    String ebean = "ModelFacade";
    String bean = "model";
    Vector atts = entity.getAttributes();
    String elist = ename.toLowerCase() + "List";
    String getlist = "searchBy" + ename + oprole;
    String pnme = "searchBy" + ename + oprole + "Field"; 
	
    out.println("package " + systemName + ";");
    out.println(); 
    out.println("import android.os.Bundle;");
    out.println("import android.app.ListActivity;");
    out.println("import android.view.View;");
    out.println("import android.widget.ArrayAdapter;");
    out.println("import android.widget.ListView;");
    out.println("import android.widget.TextView;");
    out.println("import java.util.ArrayList;");
    out.println("import java.util.List;");
    out.println();
    out.println("public class " + evc + " extends ListActivity");
    out.println("{ private ModelFacade model;"); 
    out.println();
    out.println("  ArrayList<" + evo + "> " + elist + ";"); 
    
	String tfnme = pnme + "Text"; 
    String dname = pnme + "Data";
	String lcname = (ename + oprole).toLowerCase(); 
	
    out.println("  EditText " + tfnme + ";");
    out.println("  String " + dname + " = \"\";");
    out.println();
    out.println("  @Override");
    out.println("  protected void onCreate(Bundle savedInstanceState)");
    out.println("  { super.onCreate(savedInstanceState);");
    out.println("    setContentView(R.layout.searchby" + lcname + "_layout);");
    out.println("    model = ModelFacade.getInstance(this);");
    out.println("    " + tfnme + " = (EditText) findViewById(R.id." + pnme + ");");
    out.println("    " + elist + " = new ArrayList<String>();");
    out.println("    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1," + elist + ");");
    out.println("    setListAdapter(adapter);");
    out.println("  }");
    out.println();

    out.println("  public void searchBy" + ename + oprole + "OK(View _v) ");
    out.println("  {");
    out.println("    " + dname + " = " + tfnme + ".getText() + \"\";");
	out.println("    " + elist + " = " + evo + 
         ".getStringList(model.searchBy" + ename + oprole + "(" + dname + "));"); 
	out.println("  }"); 
	out.println(); 
    out.println("  public void searchBy" + ename + oprole + "Cancel(View _v) ");
    out.println("  { } // go back to main screen");
 	out.println(); 
    out.println("  public void onListItemClick(ListView parent, View v, int position, long id)"); 
    out.println("  { model.setSelected" + ename + "(position); }");
    out.println();
    out.println("}");
  }

  public static void generateSessionBeans(Vector ents, Vector ucs)
  { 
    for (int i = 0; i < ents.size(); i++)
    { Entity e = (Entity) ents.get(i);
      Vector eucs = new Vector();
      for (int j = 0; j < ucs.size(); j++)
      { if (ucs.get(j) instanceof OperationDescription)
        { OperationDescription uc = 
            (OperationDescription) ucs.get(j);
          if (uc.getEntity() == e)
          { eucs.add(uc); }
        } 
      }

      String ename = e.getName(); 
      String sbean = ename + "SessionBean.java"; 
      File sbeanf = new File("output/" + sbean); 
      try
      { PrintWriter sbeanout = new PrintWriter(
                                     new BufferedWriter(
                                       new FileWriter(sbeanf)));
        sbeanout.println(e.getSessionBean(eucs));
        sbeanout.close(); 
      } catch (Exception _ex) { }      
    }
  }

}
