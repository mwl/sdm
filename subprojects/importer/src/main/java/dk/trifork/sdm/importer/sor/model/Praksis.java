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

package dk.trifork.sdm.importer.sor.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.Calendar;

@Output
public class Praksis extends AbstractStamdataEntity {
    private Calendar validFrom;
    private String navn;
    private Long eanLokationsnummer;
    private Long regionCode;
    private Long sorNummer;
    private Calendar validTo;
   

    public Praksis() {
    }

    @Output
    public String getNavn() {
        return navn;
    }
    public void setNavn(String navn) {
        this.navn = navn;
    }

    @Output
    public Long getEanLokationsnummer() {
		return eanLokationsnummer;
	}
	public void setEanLokationsnummer(Long eanLokationsnummer) {
		this.eanLokationsnummer = eanLokationsnummer;
	}
	

    @Output
    public Long getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(Long regionCode) {
		this.regionCode = regionCode;
	}

	@Id
    @Output
    public Long getSorNummer() {
        return sorNummer;
    }
    public void setSorNummer(Long sorNummer) {
        this.sorNummer = sorNummer;
    }

    @Override
    public Calendar getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(Calendar validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public Calendar getValidTo() {
        return (validTo != null) ? validTo : FUTURE;
    }

    public void setValidTo(Calendar validTo) {
        this.validTo = validTo;
    }

}
