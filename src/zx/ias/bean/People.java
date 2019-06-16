package zx.ias.bean;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * PeoplePOJO
 */

public class People implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private SexType sex;
    private LocalDate birthday;
    private String address;

    /**
     * 默认值
     */
    public People() {
        this.name = "N";
        this.sex = SexType.FEMALE;
        this.birthday = LocalDate.now();
        this.address = "A";
    }

    public People(String name, SexType sex, LocalDate birthday, String address) {
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
    }

    /**
     * 取可读性别字符串
     *
     * @param sexType {@link SexType}
     * @return “男“或者”女“
     */
    public static String getReadableSexString(SexType sexType) {
        String[] sexList = {"男", "女"};
        if (sexType == null) return Arrays.toString(sexList);
        return sexList[sexType.ordinal()];
    }

    /**
     * getReadableSexString反向
     *
     * @param string 　“男“或者其他
     * @return {@link SexType)
     */
    public static SexType getSexTypeFromString(String string) {
        if (string != null && string.equals("男"))
            return SexType.MALE;
        else return SexType.FEMALE;
    }

    @Override
    public String toString() {
        return "People{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SexType getSex() {
        return sex;
    }

    public void setSex(SexType sex) {
        this.sex = sex;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public enum SexType {
        MALE, FEMALE
    }
}
