import java.util.Vector; 

/* Package: Requirements Engineering */ 
/******************************
* Copyright (c) 2003-2023 Kevin Lano
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
* *****************************/

public class NLPWord extends NLPPhraseElement
{ 
  String text = ""; 
  String root = ""; // The lemma or base form of the word
  NLPWord qualifies = null;  // If this is an adjectival modifier of the qualifies word.
  int index = 0; // the word position in the original sentence.  
  
  static char[] consonants = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'}; 
  static char[] vowels = {'a', 'e', 'i', 'o', 'u', 'y'}; 

  public NLPWord(String tg, String wd)
  { super(tg); 
    text = wd; 
  }

  public String toString()
  { return "(" + tag + " " + text + ")_" + index; }  

  public String literalForm()
  { return text; }  

  public static String literalForm(Vector words)
  { String res = ""; 
    for (int i = 0; i < words.size(); i++) 
    { NLPWord wd = (NLPWord) words.get(i); 
      res = res + " " + wd.text; 
    }
    return res; 
  }   

  public static boolean isConsonant(char c)
  { for (int i = 0; i < consonants.length; i++) 
    { if (c == consonants[i]) 
      { return true; } 
    } 
    return false; 
  } 

  public static boolean isVowel(char c)
  { for (int i = 0; i < vowels.length; i++) 
    { if (c == vowels[i]) 
      { return true; } 
    } 
    return false; 
  } 

  public void linkToPhrases(NLPSentence s)
  { sentence = s; }
  
  public int indexing(int st) 
  { index = st; 
    return index + 1; 
  } 

  public void setIndex(int i)
  { index = i; } 

  public boolean isVerbPhrase() 
  { return false; } 

  public Vector sequentialise() 
  { Vector res = new Vector(); 
    res.add(this); 
    return res; 
  } 

  public boolean isInputPhrase()
  { boolean res = false; 
    String lctext = text.toLowerCase(); 

    if (lctext.startsWith("input") || 
        lctext.equals("receives") ||
        lctext.equals("requires") || 
        lctext.startsWith("parameter"))
    { return true; }
  
    return res;
  } 

  public boolean isOutputPhrase()
  { boolean res = false; 
  
    String lctext = text.toLowerCase(); 

    if (lctext.startsWith("return") || 
        lctext.startsWith("result") ||
        lctext.equals("sends") || 
        lctext.equals("delivers") || 
        lctext.startsWith("output"))
    { return true; }

    return res; 
  } 


  public boolean isKeyword() 
  { return text.equalsIgnoreCase("integer") || text.equalsIgnoreCase("numeric") || 
           text.equalsIgnoreCase("set") || text.equalsIgnoreCase("sequence") || 
           text.equals("instance") || text.equals("instances") ||
           text.equals("boolean") ||
           text.equalsIgnoreCase("real") || text.equalsIgnoreCase("double"); 
  } // or collection

  public static boolean isKeyword(String txt) 
  { return txt.equalsIgnoreCase("integer") || txt.equalsIgnoreCase("numeric") || 
           txt.equalsIgnoreCase("set") || txt.equalsIgnoreCase("sequence") || 
           txt.equals("boolean") ||
		txt.equals("instance") || txt.equals("instances") || 
           txt.equalsIgnoreCase("real") || txt.equalsIgnoreCase("double"); 
  } 

  public boolean isQualifier() 
  { return tag.equals("CD") || 
           text.equalsIgnoreCase("integer") || text.equalsIgnoreCase("numeric") || 
           text.equalsIgnoreCase("set") || text.equalsIgnoreCase("sequence") || 
		text.equalsIgnoreCase("collection") ||  
           text.equalsIgnoreCase("real") || text.equalsIgnoreCase("unique") ||
           text.equalsIgnoreCase("many") || text.equalsIgnoreCase("series") ||
           text.equalsIgnoreCase("several") || text.equalsIgnoreCase("list") || 
           text.equalsIgnoreCase("double") || (text.equalsIgnoreCase("ordered") && !isVerb()) ||
		text.equalsIgnoreCase("more") || text.equalsIgnoreCase("some") || 
           text.equals("boolean") || 
           text.equalsIgnoreCase("multiple"); 
  } // or collection



  public boolean isNounOrAdjective()
  { return isNoun() || isAdjective(); } 

  public boolean isNoun()
  { if (tag.equals("NNPS") || tag.equals("NNS") ||
        tag.equals("NN") || tag.equals("NNP"))
    { return true; } 
    return false; 
  } 

  public boolean isProperNoun()
  { if (tag.equals("NNPS") || tag.equals("NNP"))
    { return true; } 
    return false; 
  } 


  public boolean isPlural()
  { if (tag.equals("NNPS") || tag.equals("NNS"))
    { return true; } 
    return false; 
  } 

  public boolean isAdjective()
  { if (tag.equals("JJ") || tag.equals("JJS") ||
        tag.equals("JJR"))
    { return true; } 
    return false; 
  } 

  public boolean isNounPhraseWord()
  { if (tag.equals("DT") || tag.equals("PDT") || 
        tag.equals("NNPS") || tag.equals("WDT") ||
        tag.equals("WH") || tag.equals("FW") ||
        tag.equals("JJR") || 
        tag.equals("CD") || tag.equals("NNS") || 
        tag.equals("_LS") || tag.equals("JJS") ||
        tag.equals("NN") || tag.equals("NNP") || 
        tag.equals("JJ") || tag.equals("PRP") || 
        tag.equals("PRP$") || tag.equals("WP$"))
    { return true; } 
    return false; 
  } 


  public boolean isConditional()
  { String lctext = text.toLowerCase(); 
    return ("if".equals(lctext) || "when".equals(lctext) ||
            "normally".equals(lctext) || "otherwise".equals(lctext) ||
            "unless".equals(lctext)); 
  } 

  
  public boolean isSignificantVerbPhraseWord(java.util.Map verbClassifications, Vector quals, java.util.Map wordQuals)
  { String lctext = text.toLowerCase(); 

    String classif = (String) verbClassifications.get(lctext); 
    if (isVerb() && classif != null) 
    { quals.add(classif); 
      wordQuals.put(text, classif);  
      return true; 
    } 
  
    return isSignificantVerbPhraseWord(quals,wordQuals); 
  } 

  public boolean isSignificantVerbPhraseWord(Vector quals, java.util.Map wordQuals)
  { String lctext = text.toLowerCase(); 

    if (lctext.equals("write") || 
        lctext.equals("overwrite") ||
       (lctext.startsWith("edit") && isVerb()) || 
       (lctext.startsWith("confirm") && isVerb()) ||
       (lctext.equals("set") && isVerb()) || 
       (lctext.startsWith("insert") && isVerb()) ||
       (lctext.equals("schedule") && isVerb()) || 
        lctext.equals("upload") || lctext.equals("suspend") || 
       (lctext.startsWith("packag") && isVerb()) ||
       (isVerb() && 
        (lctext.startsWith("updat") ||
         lctext.startsWith("open") || 
         lctext.startsWith("clos") || 
         lctext.equals("fix") ||
         lctext.startsWith("assign") || 
         lctext.equals("reorder") || 
         lctext.equals("order") ||
         lctext.startsWith("sort") ||  
         lctext.equals("sets") || 
         lctext.startsWith("rewrit") || 
         lctext.equals("revise") || 
         lctext.equals("undelete") ||
         lctext.startsWith("rotat") ||
         lctext.startsWith("clear") || 
         lctext.equals("rename") || 
         lctext.startsWith("annotat") || 
         lctext.startsWith("approv") || 
         lctext.startsWith("certif") || 
         lctext.equals("restore") || 
         lctext.equals("revert") ||
         lctext.equals("join") || lctext.equals("undo") || 
         lctext.startsWith("correct") || 
         lctext.equals("associate") ||  
         lctext.equals("rejoin") || 
         lctext.startsWith("validat") || 
         lctext.startsWith("improv") || 
         lctext.equals("version") || 
         lctext.equals("moderate") || 
         lctext.equals("duplicate") || 
         lctext.startsWith("email") || 
         lctext.equals("post") ||   
         lctext.equals("load") || 
         lctext.equals("republish") || 
         lctext.startsWith("tag") || 
         lctext.startsWith("sign") ||
         lctext.startsWith("reproduc") || 
         lctext.startsWith("deposit") || 
         lctext.startsWith("segment") || 
         lctext.startsWith("redact") || 
         lctext.startsWith("partition") || 
         lctext.startsWith("compos") || 
         lctext.equals("say") || lctext.equals("flag") ||
         lctext.startsWith("upgrad") || 
         lctext.equals("migrate") || 
         lctext.startsWith("link") || 
         lctext.startsWith("manag") || 
         lctext.startsWith("replicat") || 
         lctext.equals("queue") || 
         lctext.startsWith("encrypt") || 
         lctext.startsWith("grant") || 
         lctext.equals("merg") || lctext.equals("combin") || 
         lctext.startsWith("customiz") || 
         lctext.startsWith("customis"))) 
       || lctext.equals("enqueue") || 
      (lctext.equals("make") && isVerb()) || 
         lctext.equals("expose") || lctext.equals("move") ||     
         lctext.equals("dequeue") || 
         lctext.equals("subscribe") || 
       (lctext.startsWith("copy") && isVerb()) || 
         lctext.equals("restock") || 
         lctext.equals("attach") || 
         lctext.equals("detach") || 
         lctext.equals("setting") || 
         lctext.startsWith("reset") || 
         lctext.startsWith("append") || 
         lctext.equals("announce") || 
         lctext.startsWith("resubmit") || 
         lctext.equals("hide") || lctext.equals("prepend") || 
         lctext.equals("conceal") || 
       (lctext.startsWith("chang") && isVerb()) || 
       (lctext.startsWith("modif") && isVerb()))
    { quals.add("edit"); 
      wordQuals.put(text, "edit");  
      return true; 
    } 

    if (isVerb() && 
	    (lctext.startsWith("creat") || lctext.startsWith("add") || 
	     lctext.startsWith("initialis") || lctext.equals("install") || lctext.equals("initiate") || 
	     lctext.equals("enroll") || lctext.equals("register") ||
		 lctext.equals("hire") ||
		 lctext.startsWith("onboard") || lctext.startsWith("submit") || lctext.startsWith("send") || 
		 lctext.equals("issue")))
    { quals.add("create"); 
      wordQuals.put(text, "create");  
      return true; 
    } 

    if ((isVerb() && 
	     (lctext.startsWith("read") || lctext.startsWith("show") || lctext.startsWith("brows") || lctext.startsWith("check") ||
		  lctext.startsWith("query") || lctext.startsWith("distinguish") || lctext.startsWith("stream") || lctext.startsWith("broadcast") ||
		  lctext.startsWith("search") || lctext.equals("track") || lctext.startsWith("navigat") || lctext.startsWith("harvest") || 
	      lctext.equals("monitor") || lctext.equals("compare") || lctext.equals("enquire") || lctext.startsWith("mine") || 
		  lctext.equals("contrast") || lctext.startsWith("inspect") || lctext.equals("extract") ) ) || 
		lctext.equals("analyse") || lctext.equals("derive") ||  
		(isVerb() && 
		  (lctext.startsWith("view") || 
              lctext.equals("learn") || 
              lctext.equals("scroll") || 
              lctext.equals("preview") ||
		   lctext.startsWith("wrangl") || 
              lctext.startsWith("display") || 
              lctext.startsWith("list") || 
              lctext.startsWith("locat") || 
		   lctext.startsWith("receiv") || 
              lctext.equals("review") || 
              lctext.startsWith("retriev") || 
              lctext.startsWith("select") ||  
		   lctext.startsWith("access") || 
              lctext.startsWith("test") || 
              lctext.startsWith("authenticat") || 
              lctext.startsWith("zoom") ||
		   lctext.startsWith("download") || 
              lctext.equals("survey") || 
              lctext.startsWith("scan") || 
              lctext.startsWith("get") || 
		   lctext.startsWith("discover") || 
              lctext.startsWith("choose"))) || 
		(lctext.startsWith("publish") && isVerb()) || 
              lctext.equals("see") || 
              lctext.startsWith("visualiz") || 
              lctext.startsWith("visualis") || 
           (lctext.startsWith("surf") && isVerb()) || 
           (lctext.startsWith("detect") && isVerb()) ||
		(lctext.startsWith("know") && isVerb()) || 
           (lctext.startsWith("explor") && isVerb()) ||
		(lctext.equals("chart") && isVerb()) || 
           (lctext.equals("identify") && isVerb()) ||
		(lctext.startsWith("requ") && isVerb()) || 
           (lctext.startsWith("examin") && isVerb()) ||
		  lctext.startsWith("debug") ||  
             lctext.startsWith("investig") ||
             lctext.equals("troubleshoot") ||  
             lctext.equals("reexamine") || 
             lctext.equals("reassess") || 
             lctext.equals("rectify") ||  
		(lctext.startsWith("assess") && isVerb()) || 
           (lctext.startsWith("measur") && isVerb()) ||
		(lctext.startsWith("find") && isVerb()) )
    { quals.add("read"); 
      wordQuals.put(text, "read");  

      return true; 
    } 


    if ((lctext.startsWith("delet") && isVerb()) || 
        (lctext.startsWith("remov") && isVerb()) || 
        (lctext.startsWith("eras") && isVerb()) ||
        (lctext.equals("cancel") && isVerb()) || 
        lctext.equals("unpublish") || 
        (lctext.startsWith("cut") && isVerb()) || 
	   lctext.equals("terminate") || 
        (lctext.equals("fire") && isVerb()) ||
	   lctext.startsWith("destroy") )
    { quals.add("delete");
      wordQuals.put(text, "delete");  
 
      return true; 
    } 

    if (isVerb() && 
	    (lctext.startsWith("stor") ||
          lctext.startsWith("record") || 
          lctext.startsWith("save") || 
          lctext.startsWith("curat") ||
          lctext.startsWith("archiv") || 
          lctext.startsWith("persist") || 
          lctext.startsWith("preserv") || 
          lctext.startsWith("commit"))) 
    { quals.add("persistent");
      quals.add("edit"); 
      wordQuals.put(text, "persistent");  
      return true; 
    }  // also counts as an edit

    if ((lctext.startsWith("communicat") && isVerb()) || 
         lctext.equals("deny") ||
	    (lctext.startsWith("lift") && isVerb()) || (lctext.startsWith("elevat") && isVerb()) || lctext.equals("allow") || 
	    (lctext.startsWith("model") && isVerb()) || (lctext.startsWith("login") && isVerb()) || (lctext.startsWith("restrict") && isVerb()) ||
	    lctext.startsWith("prioritis") || 
         (lctext.startsWith("import") && isVerb()) || 
         (lctext.startsWith("function") && isVerb()) || 
         (isVerb() && 
           (lctext.startsWith("execut") || 
            lctext.startsWith("run") ||  
            lctext.startsWith("signup") ||
            lctext.startsWith("delimit") ||
            lctext.startsWith("broker") || 
            lctext.startsWith("negotiat") || 
            lctext.startsWith("filter") || 
            lctext.startsWith("prefilter") || 
            lctext.equals("justify") ||
            lctext.startsWith("classif") || 
            lctext.startsWith("preclassif") || 
            lctext.startsWith("group") || 
            lctext.equals("push") || 
            lctext.startsWith("tell") || 
            lctext.startsWith("deliv") ||
            lctext.equals("mint") || 
            lctext.startsWith("fulfil") || 
            lctext.startsWith("gain") || 
            lctext.startsWith("assur") || 
            lctext.startsWith("regen") || 
            lctext.startsWith("reassur") || 
            lctext.startsWith("scop") || 
            lctext.equals("ask") || 
            lctext.startsWith("recogni") ||
            lctext.startsWith("categori") || 
            lctext.startsWith("recommend") || 
            lctext.startsWith("advis") ||
		 lctext.startsWith("administ") ||
            lctext.startsWith("point") || 
            lctext.startsWith("shar") || 
            lctext.equals("interact") || 
            lctext.startsWith("signin") ||
            lctext.startsWith("compl") || 
            lctext.startsWith("calculat") ||
            lctext.startsWith("call") || lctext.startsWith("place") ) ) || 
		lctext.equals("ingest") || 
		(lctext.startsWith("respond") && isVerb()) || (lctext.startsWith("configur") && isVerb()) || lctext.equals("assist") || 
		(lctext.startsWith("export") && isVerb()) || lctext.startsWith("logout") || (lctext.equals("written") && isVerb()) || 
        lctext.startsWith("aggregat") || lctext.equals("buy") || lctext.equals("reacts") ||	lctext.startsWith("declar") || 	
	    lctext.equals("warn") || lctext.equals("warned") || lctext.equals("allocate") || (lctext.startsWith("alert") && isVerb()) || 
		lctext.startsWith("understand") || lctext.startsWith("have") || lctext.startsWith("signout") || lctext.equals("revise") ||
		(lctext.equals("sign") && isVerb()) || (lctext.equals("mark") && isVerb()) || 
		(lctext.startsWith("indicat") && isVerb()) ||
	    (lctext.equals("give") && isVerb()) || (lctext.startsWith("notif") && isVerb()) || lctext.equals("describe") ||
		(lctext.startsWith("use") && isVerb()) || lctext.startsWith("enter") || (lctext.equals("reenter") && isVerb()) ||
		(lctext.startsWith("concept") && isVerb()) || (lctext.startsWith("estimat") && isVerb()) ||
		(lctext.startsWith("attend") && isVerb()) || (lctext.startsWith("begin") && isVerb()) || lctext.equals("invite") || 
		lctext.equals("retake") || lctext.equals("resit") || lctext.equals("revisit") || lctext.equals("renew") || 
		lctext.equals("satisfy") || lctext.equals("prepare") || lctext.equals("support") || lctext.equals("capture") ||
		lctext.equals("perform") || lctext.equals("diagnose") || lctext.equals("transfer") || 
		lctext.startsWith("generat") || (lctext.startsWith("produc") && isVerb()) ||  
		(lctext.startsWith("compute") && isVerb()) || lctext.equals("demote") || 
		lctext.startsWith("normaliz") || lctext.startsWith("normalis") || (lctext.equals("log") && isVerb()) ||
		lctext.startsWith("choose") || (lctext.startsWith("invit") && isVerb()) || 
		lctext.startsWith("facilitat") || lctext.equals("encode") || lctext.equals("decode") ||  
		(lctext.startsWith("act") && isVerb()) || lctext.equals("ensure") || 
		lctext.equals("establish") ||
		(isVerb() && 
		 (lctext.startsWith("highlight") ||
             lctext.startsWith("leverage") ||
             lctext.startsWith("translat") || 
             lctext.startsWith("offer") || 
             lctext.startsWith("constr") || 
		  lctext.startsWith("redirect") || lctext.startsWith("arrang") || lctext.startsWith("plan") || lctext.startsWith("reus") || 
		  lctext.startsWith("form") || lctext.startsWith("reform") || lctext.startsWith("preinstall") || lctext.startsWith("rearrang") ||
		  lctext.startsWith("reply") || lctext.startsWith("forward") || lctext.startsWith("host") || lctext.startsWith("sync") ||
		  lctext.startsWith("alias") || lctext.startsWith("pass") || lctext.startsWith("maintain") || lctext.startsWith("promot") || 
		  lctext.startsWith("help") || lctext.startsWith("repres") || lctext.startsWith("direct") || lctext.startsWith("disseminat") ||  
		  lctext.startsWith("prov") || lctext.startsWith("limit") || lctext.equals("draw") || lctext.equals("withdraw") || 
		  lctext.startsWith("tolerat") || lctext.startsWith("enabl") || lctext.startsWith("disabl") || lctext.equals("theme") ||  
		  lctext.startsWith("forc") || lctext.equals("denote") || lctext.startsWith("conduct") || lctext.startsWith("tak") || 
		  lctext.startsWith("separat") || lctext.startsWith("depend") || lctext.startsWith("implement"))) || 
		lctext.equals("develop") || lctext.equals("evaluate") ||
		(isVerb() && 
		 (lctext.startsWith("packag") || lctext.startsWith("click") || lctext.startsWith("press") || lctext.startsWith("walk") || 
		  lctext.startsWith("contact") || lctext.equals("appear") || lctext.equals("control") || 
		  lctext.startsWith("embed") || lctext.startsWith("specif") || lctext.equals("process") || lctext.equals("trigger") ||
		  lctext.startsWith("delegat") || lctext.startsWith("apply") || lctext.startsWith("collect") || lctext.startsWith("activat") || 
		  lctext.startsWith("complet"))) ||
		lctext.equals("finalise") || lctext.equals("finalize") || lctext.equals("enact") || lctext.equals("authorise") ||
		lctext.equals("authorize") || lctext.equals("notify") || (lctext.equals("report") && isVerb()) || 
		(lctext.equals("reject") && isVerb()) || lctext.equals("reflect") || (lctext.equals("fill") && isVerb()) ||
		(lctext.equals("recruit") && isVerb()) || (lctext.equals("continue") && isVerb()) || lctext.equals("deploy") || 
		(lctext.startsWith("demonstrat") && isVerb()) || (lctext.startsWith("visit") && isVerb()) ||
		(lctext.startsWith("revisit") && isVerb()) || (lctext.startsWith("elect") && isVerb()) || 
		(lctext.startsWith("narrow") && isVerb()) || (lctext.startsWith("expan") && isVerb()) ||  
           (lctext.equals("want") && isVerb()) ||  
		(lctext.startsWith("integrat") && isVerb()) || (lctext.startsWith("map") && isVerb()) || lctext.equals("relieve") || 
		lctext.equals("pay") || (lctext.startsWith("connect") && isVerb()) || lctext.equals("keep") || lctext.equals("accept") || 
	    lctext.equals("put") || (lctext.startsWith("refer") && isVerb()) || (lctext.equals("handle") && isVerb()) || 
		(lctext.equals("leave") && isVerb()) || (lctext.equals("block") && isVerb()) || (lctext.startsWith("suggest") && isVerb()) ||
		lctext.equals("enlist") || lctext.equals("enlighten") || lctext.equals("extend") || lctext.equals("clarify") ||
		(lctext.equals("include") && isVerb()) || (lctext.equals("stop") && isVerb()) ||  
		(lctext.equals("start") && isVerb()) || (lctext.equals("design") && isVerb()) || lctext.equals("redesign") ||  
		(lctext.startsWith("calibrat") && isVerb()) || lctext.equals("determine") || lctext.equals("decide") || 
		(lctext.equals("express") && isVerb()) || (lctext.equals("meet") && isVerb()) || (lctext.equals("shadow") && isVerb()))
    { quals.add("other"); 
      wordQuals.put(text, "other");  

      return true; 
    } 

    return false; 
  } 

  public boolean isVerbPhraseWord(Vector quals, java.util.Map wordQuals)
  { boolean res = isSignificantVerbPhraseWord(quals,wordQuals); 

    if (res) { return true; } 

    if (tag.equals("VB") || tag.equals("VBZ") || tag.equals("TO") || tag.equals("VBG") || 
        tag.equals("MD") || tag.equals("IN") || tag.equals("VBD") ||
	   tag.equals("VBN") || tag.equals("VBP") || tag.equals("RB") || tag.equals("WRB") || 
	   tag.equals("EX"))
    { return true; }

    if ("SYM".equals(tag) && text.equals("="))
    { return true; } 

    return false; 
  }
  
  public boolean isVerb()
  { if (tag.equals("VB") || tag.equals("VBZ") || 
        tag.equals("TO") || tag.equals("VBG") || 
        tag.equals("VBD") ||
        tag.equals("VBN") || tag.equals("VBP"))
    { return true; }
    return false; 
  } 

  public boolean isPureVerb()
  { if (tag.equals("VB") || tag.equals("VBZ") || 
        tag.equals("VBG") || 
        tag.equals("VBD") ||
        tag.equals("VBN") || tag.equals("VBP"))
    { return true; }
    return false; 
  } 

  public boolean isModalVerb()
  { if (tag.equals("MD"))
    { return true; }
    return false; 
  } // should, shall, must, will, etc. 

  public boolean isConjunctionWord()
  { if (tag.equals("IN") || tag.equals("CC"))
    { return true; }
    return false; 
  }

  public boolean isConjunction()
  { if (isSeparator() ||
        text.equalsIgnoreCase("and") || text.equalsIgnoreCase("of"))
    { return true; }
    return false; 
  }

  public boolean isPreposition()
  { if (tag.equals("IN"))
    { return true; }
    return false; 
  }

  public boolean isArticle()
  { if (tag.equals("DT"))
    { return true; }
    return false; 
  }

  public boolean isSeparator()
  { if (text.equals("+") || text.equals("&") || text.equals(",") || text.equals(";") ||
        text.equalsIgnoreCase("|") || text.equalsIgnoreCase("/"))
    { return true; }
    return false; 
  }


  public String formQualifier()
  { return text; } 
  // but not for punctuation, etc. 

  public String getSingular()
  { if (isPlural())
    { if (text.endsWith("ies"))
      { return text.substring(0,text.length()-3) + "y"; }
      else if (text.endsWith("shes") && text.length() > 4)
      { return text.substring(0,text.length()-2); }  
      else if (text.endsWith("ches") && text.length() > 4)
      { return text.substring(0,text.length()-2); }  
      else if (text.endsWith("s") || text.endsWith("es"))
      { return text.substring(0,text.length()-1); } 
    }
    return text; 
  } 

  /* Depricated, do not use: */
  public static String getSingular(String txt)
  { if (txt.endsWith("ies"))
    { return txt.substring(0,txt.length()-3) + "y"; } 
    else if (txt.endsWith("shes") && txt.length() > 4)
    { return txt.substring(0,txt.length()-2); } 
    else if (txt.endsWith("ches") && txt.length() > 4)
    { return txt.substring(0,txt.length()-2); } 
    else if (txt.endsWith("s") || txt.endsWith("es"))
    { return txt.substring(0,txt.length()-1); } 
    return txt; 
  }  

  public static String getPlural(String txt)
  { if (txt.endsWith("s") || txt.endsWith("x") ||
        txt.endsWith("sh") || txt.endsWith("ch"))
    { return txt + "es"; } 

    if (txt.endsWith("y") && txt.length() > 2 && 
        isConsonant(txt.charAt(txt.length()-2)))
    { String substr = txt.substring(0,txt.length()-1); 
      return substr + "ies"; 
    } 

    if (txt.endsWith("elf"))
    { String substr = txt.substring(0,txt.length()-3); 
      return substr + "elves"; 
    } 

    return txt + "s"; 
  }  

  public Vector extractVerbedNouns(java.util.Map quals, java.util.Map types, java.util.Map fromBackground, Vector currentQuals)
  { Vector nouns = new Vector(); 
    nouns.add(text); 
    return nouns; 
  } 

  public Vector extractNouns(java.util.Map quals, java.util.Map types, java.util.Map fromBackground, Vector currentQuals)
  { Vector nouns = new Vector(); 
    nouns.add(text); 
    return nouns; 
  } 


  public Type identifyType(String text, java.util.Map qm, java.util.Map types, Vector modelems)
  { Type res = null; 

    res = (Type) types.get(text);
    if (res != null) 
    { return res; } 

    Vector quals = (Vector) qm.get(text); 
    if (quals == null) 
    { quals = new Vector(); } 

    for (int i = 0; i < quals.size(); i++) 
    { String wd = (String) quals.get(i); 
      if (wd.equals("numeric") || wd.equals("real") || wd.startsWith("double") || wd.startsWith("float") ||           
          wd.startsWith("realvalue") || wd.startsWith("real-value") ||
          wd.endsWith("number")) 
      { res = new Type("double", null); } 
      else if (wd.startsWith("int"))
      { res = new Type("int", null); } 
      else if (wd.startsWith("bool"))
      { res = new Type("boolean", null); } 
    } 


    // if res is lower case & 
    // res or res singular form is a class, 
    // res is a reference.
 
    if (res == null) 
    { Object ex = ModelElement.lookupByNameIgnoreCase(text,modelems); 
      if (ex != null && ex instanceof Entity)
      { // it is a singular reference of type ex
        res = new Type((Entity) ex); 
      } 
    }  

    if (res == null && isPlural()) 
    { String sing = getSingular(); 

      Object ex = ModelElement.lookupByNameIgnoreCase(sing,modelems); 
      if (ex != null && ex instanceof Entity)
      { // it is a singular reference of type ex
        Type reselem = new Type((Entity) ex);
       
        if (quals.contains("Sequence") || quals.contains("series") || quals.contains("list") || quals.contains("ordered")) 
        { res = new Type("Sequence", null); }
        else  
        { res = new Type("Set", null); } 
        res.setElementType(reselem); 
        return res;  
      } 
    }  

    if (res == null) 
    { if ("age".equals(text) || "weight".equals(text) || 
          "height".equals(text) || "time".equals(text) || 
          "years".equals(text) || "longitude".equals(text) || 
          "latitude".equals(text) || "altitude".equals(text) || 
          "duration".equals(text) || "distance".equals(text) || 
          "radius".equals(text) || "magnitude".equals(text) ||
          "year".equals(text) || "frequency".equals(text) || 
          "velocity".equals(text) || text.startsWith("score") || 
          "acceleration".equals(text) || "speed".equals(text) || "depth".equals(text))
      { res = new Type("double", null); } 
      else if ("count".equals(text))
      { res = new Type("int", null); } 
      else 
      { res = new Type("String", null); } 
    } 

    Type elementType = res; 

    if (quals.contains("many") || quals.contains("Set") || quals.contains("collection")) 
    { res = new Type("Set", null); 
      res.setElementType(elementType); 
    } 
    else if (quals.contains("Sequence") || quals.contains("series") || quals.contains("list") || quals.contains("ordered")) 
    { res = new Type("Sequence", null); 
      res.setElementType(elementType); 
    } 

    return res; 
  } // but nouns such as age, year are always numeric. 

  public static void identifyStereotypes(Attribute att, java.util.Map qm)
  { String attname = att.getName(); 
    Vector quals = (Vector) qm.get(attname); 

    if (quals == null)
    { return; } 

    for (int i = 0; i < quals.size(); i++) 
    { String wd = (String) quals.get(i); 
      if (wd.equals("identifier") ||           
          wd.equals("key") || wd.equals("identity") ||
          wd.equals("unique")) 
      { att.setIdentity(true); } 
      else if (wd.startsWith("const") || wd.equals("readOnly") ||
               wd.equals("fixed") || wd.equals("frozen"))
      { att.setFrozen(true); } 
    } 
  } 

  public java.util.HashMap classifyVerbs(Vector verbs)
  { java.util.HashMap res = new java.util.HashMap(); 
  
    // if (isKeyword()) 
    // { return res; } 
	
    ThesaurusConcept tc = Thesarus.lookupWord(verbs,text); 
    if (tc != null && tc.verbType.length() > 0)
    { res.put(text, tc.verbType); }
	
    return res; 
  } 

  public java.util.HashMap classifyWords(Vector background, Vector modelElems)
  { java.util.HashMap res = new java.util.HashMap(); 
  
    if (isKeyword()) 
    { return res; } 
	
    ThesaurusConcept tc = Thesarus.lookupWord(background,text); 

    if (tc != null && tc.semantics.size() > 0)
    { res.put(text, tc.semantics); }
    else if (isPlural())
    { String sing = getSingular(); 
	  // System.out.println(">>> Singular of " + text + " is " + sing); 
      String cleanSing = Named.removeInvalidCharacters(sing); 

      tc = Thesarus.lookupWord(background, cleanSing); 
      if (tc != null)
      { Vector tcsem = tc.semantics; 
        if (tcsem.size() > 0 && (tcsem.get(0) instanceof Entity))
        { Type colltype = new Type("Set", null);
          String ename = Named.capitalise(cleanSing); 
          Entity ee = null; 
		  
          Object obj = ModelElement.lookupByNameIgnoreCase(ename,modelElems); 
          if (obj != null && obj instanceof Entity)
          { ee = (Entity) obj; }
          
          if (ee == null)
          { Entity ment = (Entity) ModelElement.lookupByNameIgnoreCase(ename,tc.semantics); 
            System.out.println("><><> Recognised background entity: " + ename); 

            if (ment != null) 
            { ee = (Entity) ment.clone(); 
              ee.setName(Named.capitalise(ment.getName())); 
              if (ment.getSuperclass() != null)
              { Entity msup = ment.getSuperclass(); 
                Entity nsup = (Entity) msup.clone(); 
                nsup.setName(Named.capitalise(msup.getName())); 
                ee.setSuperclass(nsup); 
                modelElems.add(ee); 
              } 
            }
          } 

          if (ee == null)  
          { ee = new Entity(ename);
            modelElems.add(ee); 
            System.out.println("><><> Recognised new entity: " + ename); 
            String id = sentence.id; 
            sentence.derivedElements.add(ee); 
            ee.addStereotype("originator=\"" + id + "\""); 
          }   

          colltype.setElementType(new Type(ee)); 
          Attribute r = new Attribute(text, colltype, ModelElement.INTERNAL); 
          Vector sem1 = new Vector(); 
          sem1.add(r); 
          res.put(text, sem1); 
        }
      }
    }
    else if (isProperNoun())
    { String ename = 
         Named.removeInvalidCharacters(getSingular()); 
      Vector sem = new Vector(); 
      Entity ee = null; 
		  
      Object obj = ModelElement.lookupByNameIgnoreCase(ename,modelElems); 
      if (obj != null && obj instanceof Entity)
      { ee = (Entity) obj; }
      else
      { ee = new Entity(ename);
        modelElems.add(ee);
        System.out.println(">>> Recognised new entity: " + ename); 
        String id = sentence.id; 
        sentence.derivedElements.add(ee); 
        ee.addStereotype("originator=\"" + id + "\""); 
      }   
      sem.add(ee); 
      res.put(text, sem); 
    }  
	// These need to be added to the model elements if they are not already there. 
	
    return res; 
  }

  public String getPrincipalNoun()
  { String noun = ""; 
    if (isNoun())
    { noun = text; } 
    return noun; 
  }   

  public NLPWord identifyNounForEntity()
  { if (isNoun())
    { return this; } 
    return null; 
  }   

  public String getMostSignificantVerb()
  { String verb = ""; 
    String lex = tag; 
    if (lex.equals("VB") || lex.equals("VBZ") || 
        lex.equals("VBG") || lex.equals("VBD") ||
        lex.equals("VBN") || lex.equals("VBP"))
    { verb = text; } 
	   
    return verb; 
  }  // Actually the textually last verb. 

  public void extractAssociationDefinitions(Entity ent, String role, java.util.Map fromBackground, Vector modelElements)
  { extractClassReferences(ent,role, fromBackground, modelElements); } 

  public void extractRelationshipDefinitions(Entity ent, Vector modelElements)
  { if (isNoun())
    { String singular = getSingular();
      String cleanName = 
          Named.removeInvalidCharacters(singular);  
 
      Entity entnew = 
        (Entity) ModelElement.lookupByNameIgnoreCase(
                                cleanName, modelElements); 
      if (entnew == null) 
      { entnew = new Entity(Named.capitalise(cleanName));
        modelElements.add(entnew);
        String id = sentence.id; 
        entnew.addStereotype("originator=\"" + id + "\""); 
        sentence.derivedElements.add(entnew);
      }
      entnew.setSuperclass(ent);
      ent.addSubclass(entnew);  
      System.out.println(">>> alternative class: " + entnew.getName()); 
    }
  }
   
  public void extractClassReferences(Entity ent, String role, java.util.Map fromBackground, Vector modelElements)
  { 
    String attname =  
          Named.removeInvalidCharacters(text);  

    if (NLPWord.isKeyword(attname)) 
    { return; } 
	
    if (isPlural())
    { attname = getSingular(); }
	
    Entity tent = (Entity) ModelElement.lookupByNameIgnoreCase(attname,modelElements); 
    if (tent != null) 
    { System.out.println(">>> Existing class: " + attname); }
    else 
    { tent = new Entity(Named.capitalise(attname)); 
      System.out.println(">>> Creating new class: " + attname);
      modelElements.add(tent);  
      String id = sentence.id; 
      sentence.derivedElements.add(tent); 
      tent.addStereotype("originator=\"" + id + "\""); 
    }

    String role2 = attname.toLowerCase();
    if (role != null) 
    { role2 = role; }
			
    int card1 = ModelElement.MANY; 
    int card2 = ModelElement.ONE;  
	
    if (isPlural()) // or if the object is plural
    { card2 = ModelElement.MANY; }
	
    if (ent.hasRole(role2))
    { System.err.println("!! Possible conflict in requirements: role " + role2 + " of class " + attname + " already exists"); 
      role2 = role2 + "_" + ent.getAssociations().size(); 
    }

    Association newast = new Association(ent,tent,card1,card2,"",role2);   
    System.out.println(">>> new association " + newast + " for class " + ent.getName()); 
    ent.addAssociation(newast); 
  } 

  public static void main(String[] args)
  { System.out.println(NLPWord.getSingular("Classes")); 
    System.out.println(NLPWord.getPlural("Class")); 
    System.out.println(NLPWord.getPlural("lash")); 
    System.out.println(NLPWord.getPlural("ice")); 
    System.out.println(NLPWord.getPlural("boy")); 
    System.out.println(NLPWord.getPlural("try")); 
    System.out.println(NLPWord.getPlural("self")); 
  } 

} 