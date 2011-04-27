// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Indholdsstoffer extends TakstEntity {

    private Long drugID;        //Ref. t. LMS01
    private Long varenummer;        //Ref. t. LMS02
    private String stofklasse;
    private String substansgruppe;
    private String substans;        //Kun aktive substanser

    @Output
    public Long getDrugID() {
        return this.drugID;
    }

    public void setDrugID(Long drugID) {
        this.drugID = drugID;
    }

    @Output
    public Long getVarenummer() {
        return this.varenummer;
    }

    public void setVarenummer(Long varenummer) {
        this.varenummer = varenummer;
    }

    @Output
    public String getStofklasse() {
        return this.stofklasse;
    }

    public void setStofklasse(String stofklasse) {
        this.stofklasse = stofklasse;
    }

    @Output
    public String getSubstansgruppe() {
        return this.substansgruppe;
    }

    public void setSubstansgruppe(String substansgruppe) {
        this.substansgruppe = substansgruppe;
    }

    @Output
    public String getSubstans() {
        return this.substans;
    }

    public void setSubstans(String substans) {
        this.substans = substans;
    }

    @Id
    @Output(name = "CID")
    public String getKey() {
        return substans + "-" + substansgruppe + "-" + stofklasse + "-" + drugID;
    }


    public boolean equals(Object o) {
        if (o.getClass() != Indholdsstoffer.class)
            return false;
        Indholdsstoffer stof = (Indholdsstoffer) o;
        return getKey().equals(stof.getKey());

    }

}