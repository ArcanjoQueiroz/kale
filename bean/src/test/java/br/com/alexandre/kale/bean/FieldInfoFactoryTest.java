package br.com.alexandre.kale.bean;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class FieldInfoFactoryTest {

  private FieldInfoFactory fieldInfoFactory;

  @Before
  public void setUp() {
    this.fieldInfoFactory = new FieldInfoFactory();
  }

  @Test
  public void shouldCreateAlistOfFieldInfo() {
    class MyBean implements Serializable {

      private static final long serialVersionUID = 1840560010694686985L;

      @LessThan private Long id;
      private String name;
      private Character sex;
      private Date birth;

      public MyBean() {}

      @SuppressWarnings("unused")
      public Long getId() {
        return id;
      }

      public void setId(Long id) {
        this.id = id;
      }

      @SuppressWarnings("unused")
      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      @SuppressWarnings("unused")
      public Character getSex() {
        return sex;
      }

      public void setSex(Character sex) {
        this.sex = sex;
      }

      @SuppressWarnings("unused")
      public Date getBirth() {
        return birth;
      }

      @SuppressWarnings("unused")
      public void setBirth(Date birth) {
        this.birth = birth;
      }

      @Override
      public String toString() {
        return "MyBean [id=" + id + ", name=" + name + ", sex=" + sex + ", birth=" + birth + "]";
      }
    }

    final MyBean myBean = new MyBean();
    myBean.setId(1L);
    myBean.setName("Foo");
    myBean.setSex('M');

    final List<FieldInfo> actual = fieldInfoFactory.createFieldInfo(myBean);
    assertThat(actual).isNotNull();
    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual)
        .contains(
            new FieldInfo("id", "<", 1L),
            new FieldInfo("name", "=", "Foo"),
            new FieldInfo("sex", "=", "M"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowAnExceptionIfTheArgumentIsNull() {
    fieldInfoFactory.createFieldInfo(null);
  }
}
