/******************************
* Copyright (c) 2003-2023 Kevin Lano
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
* *****************************/
/* Package ATL */ 

abstract public class ModuleElement
{ String name; 

  public String getName()
  { return name; } 

  public ModuleElement() { } 

  public ModuleElement(String nme)
  { name = nme; } 

  public void setName(String nme) 
  { name = nme; } 
}