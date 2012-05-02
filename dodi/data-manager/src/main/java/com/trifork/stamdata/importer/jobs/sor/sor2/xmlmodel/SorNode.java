/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

package com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel;

import java.sql.SQLException;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.RecordPersister;

public abstract class SorNode {

	protected Vector<SorNode> children = new Vector<SorNode>();
	private SorNode parentNode;
	private String parentTag;
	
	private Long PID;
	
	protected boolean dirty;
	private boolean hasUniqueKey;
	
	public SorNode(Attributes attribs, SorNode parent, String parentTag) {
		this.parentNode = parent;
		this.setParentTag(parentTag);
		dirty = false;
	}
	
	public void persist(RecordPersister persister) throws SQLException {
		for (SorNode node : children) {
			node.persist(persister);
		}
	}
	
	public SorNode getParentNode() {
		return parentNode;
	}
	
	abstract public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher) throws SQLException;
	
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		return false;
	}
	
	public void addChild(SorNode child) {
		this.children.addElement(child);
	}
	
	public void removeChild(SorNode child) {
		this.children.removeElement(child);
	}

	@Override
	public String toString() {
		return "XmlNode [children=" + children + "]";
	}

	public boolean isUniqueKey() {
		return hasUniqueKey;
	}

	public void setHasUniqueKey(boolean hasUniqueKey) {
		this.hasUniqueKey = hasUniqueKey;
	}

	public Long getPID() {
		return PID;
	}

	public void setPID(Long pID) {
		PID = pID;
	}

	public String getParentTag() {
		return parentTag;
	}

	public void setParentTag(String parentTag) {
		this.parentTag = parentTag;
	}

}