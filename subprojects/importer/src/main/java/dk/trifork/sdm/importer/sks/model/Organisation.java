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

package dk.trifork.sdm.importer.sks.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.Calendar;

public class Organisation extends AbstractStamdataEntity {
    private Calendar validFrom;
    private Calendar validTo;
    private String navn;
    private String nummer;

    public enum Organisationstype {
        Afdeling, Sygehus
    }


    private final Organisationstype organisationstype;


    public Organisation(Organisationstype organisationstype) {
        this.organisationstype = organisationstype;
    }

    public Calendar getValidTo() {
        return validTo;
    }


    public void setValidTo(Calendar validTo) {
        this.validTo = validTo;
    }


    @Output
    public String getNavn() {
        return navn;
    }


    public void setNavn(String navn) {
        this.navn = navn;
    }


    @Id
    @Output
    public String getNummer() {
        return nummer;
    }


    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    @Output
    public String getOrganisationstype() {
        if (organisationstype == Organisationstype.Afdeling)
            return "Afdeling";
        else if (organisationstype == Organisationstype.Sygehus)
            return "Sygehus";
        return null;
    }


    public void setValidFrom(Calendar validFrom) {
        this.validFrom = validFrom;
    }


    @Override
    public Calendar getValidFrom() {
        return validFrom;
    }


}
