package kz.kacd.sso.external.migration.account.kafka.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DrscbPersonRepresentation {

    private BigDecimal id;

    private String irs;

    private String bin;
    private String iin;
    private String idn;

    private Long regDate;
    private Long dateEnd;

    private String rusName;
    private String firstNameRus;
    private String lastNameRus;
    private String midNameRus;

    private String phone;
    private String mPhone;
    private String tel;
    private String email;

    private List<DrscbRight> rights;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getIrs() {
        return irs;
    }

    public void setIrs(String irs) {
        this.irs = irs;
    }

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getIdn() {
        return idn;
    }

    public void setIdn(String idn) {
        this.idn = idn;
    }

    public Long getRegDate() {
        return regDate;
    }

    public void setRegDate(Long regDate) {
        this.regDate = regDate;
    }

    public Long getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Long dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getRusName() {
        return rusName;
    }

    public void setRusName(String rusName) {
        this.rusName = rusName;
    }

    public String getFirstNameRus() {
        return firstNameRus;
    }

    public void setFirstNameRus(String firstNameRus) {
        this.firstNameRus = firstNameRus;
    }

    public String getLastNameRus() {
        return lastNameRus;
    }

    public void setLastNameRus(String lastNameRus) {
        this.lastNameRus = lastNameRus;
    }

    public String getMidNameRus() {
        return midNameRus;
    }

    public void setMidNameRus(String midNameRus) {
        this.midNameRus = midNameRus;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<DrscbRight> getRights() {
        return rights;
    }

    public void setRights(List<DrscbRight> rights) {
        this.rights = rights;
    }
}
