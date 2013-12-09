/*
 * Copyright (C) 2013 by Tramaci.Org
 * This file is part of OnionMail (http://onionmail.info)
 * 
 * OnionMail is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.tramaci.onionmail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class DynaRes {
		public HashMap <String,String> Head = new HashMap <String,String>();
		public HashMap <String,String> Par = new HashMap <String,String>();
		public String Res = "";
		
		public static DynaRes GetHinstance(Config C,String rs) throws Exception {
			if (!rs.matches("[0-9A-Za-z\\-\\_]{1,32}")) throw new Exception("Invalid resource file");
			
			if (C.ResPath!=null) {
				String f = J.MapPath(C.ResPath, rs+".res");
				if (new File(f).exists()) return new DynaRes(Stdio.file_get_bytes(f));
				}
			
			InputStream I= C.getClass().getResourceAsStream("resources/"+rs+".res");
			if (I==null) throw new Exception("Unknown resource `"+rs+"`");
			return new DynaRes(I);
			
		}
		
		DynaRes() {}
		
		DynaRes(String Filename) throws Exception {
			FileInputStream I = new FileInputStream(Filename);
			Load(I);
			I.close();
			}
		
		DynaRes(InputStream I) throws Exception {
			Load(I);
			I.close();
			}
		
		DynaRes(BufferedReader I) throws Exception {
			Load(I);
			I.close();
			}
		
		DynaRes(byte[] b) throws Exception { Load(J.getLineReader8(new ByteArrayInputStream(b))); }
		
		public String toString() {
			String q = J.CreateHeaders(Head);
			q+="\n"+J.CreateHeaders(Par);
			q+="\n"+Res;
			return q;
		}
		
		public byte[] getBytes() { return toString().getBytes(); }
		
		private void Load(InputStream I) throws Exception {
				BufferedReader S = J.getLineReader8(I);
				Load(S);
				}
		
		
		private void Load(BufferedReader I) throws Exception {
				Head = J.ParseHeadersEx(I);
				Par = J.ParseHeadersEx(I);
				Res="";
				while(true) {
					String li=I.readLine();
					if (li==null) break;
					Res+=li+"\n";
					}
			}
					
		public DynaRes Get() {
			String tmp = Res;
			HashMap <String,String> H = new HashMap <String,String>();
			for(String K:Par.keySet()) tmp=tmp.replace("%"+K+"%",Par.get(K));
			for(String K:Head.keySet()) {
				String v = Head.get(K);
				if (v.contains("%")) {
					for(String K1:Par.keySet()) if (K1.startsWith("h_")) v=v.replace("%"+K1+"%",Par.get(K1));
					}
				H.put(K, v);
				}
		DynaRes D = new DynaRes();
		D.Head=H;
		D.Par=Par;
		D.Res=tmp;
		return D;
		}
}