package de.neuland.firefly.changes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GroovyChangeTest {
    @Mock
    private GenericApplicationContext applicationContext;
    @Mock
    private Log4JPrintStream out;

    @Before
    public void setUp() {
        given(applicationContext.getBeanDefinitionNames()).willReturn(new String[0]);
    }

    @Test
    public void shouldBindApplicationContextForVersionsUpTo5() {
        // when
        Map<String, Object> shellContext = GroovyChange.createShellContext(applicationContext, out);

        // then
        assertEquals(shellContext.get("ctx"), applicationContext);
    }

    @Test
    public void shouldBindApplicationContextForVersions6() {
        // when
        Map<String, Object> shellContext = GroovyChange.createShellContext(applicationContext, out);

        // then
        assertEquals(shellContext.get("spring"), applicationContext);
    }

    @Test
    public void shouldBindSpringBeans() {
        // given
        TestBean bean1 = prepareBean("bean1");
        TestBean bean2 = prepareBean("bean2");
        given(applicationContext.getBeanDefinitionNames()).willReturn(new String[]{
            bean1.name(),
            bean2.name()
        });

        // when
        Map<String, Object> shellContext = GroovyChange.createShellContext(applicationContext, out);

        // then
        assertEquals(shellContext.get(bean1.name()), bean1);
        assertEquals(shellContext.get(bean2.name()), bean2);
    }

    @Test
    public void shouldNotBindAbstractSpringBeans() {
        // given
        TestBean bean = prepareBean("bean1");
        TestBean abstractBean = prepareBean("abstractBean", true);
        given(applicationContext.getBeanDefinitionNames()).willReturn(new String[]{
            bean.name(),
            abstractBean.name()
        });

        // when
        Map<String, Object> shellContext = GroovyChange.createShellContext(applicationContext, out);

        // then
        assertFalse(shellContext.containsKey("abstractBean"));
    }

    @Test
    public void shouldNotBindSpringBeansWithQualifiedName() {
        // given
        TestBean bean = prepareBean("bean");
        TestBean qualified = prepareBean("package.bean");
        given(applicationContext.getBeanDefinitionNames()).willReturn(new String[]{
            bean.name(),
            qualified.name()
        });

        // when
        Map<String, Object> shellContext = GroovyChange.createShellContext(applicationContext, out);

        // then
        assertFalse(shellContext.containsKey("abstractBean"));
    }

    @Test
    public void shouldNotBindSpringBeansUnderAlias() {
        // given
        TestBean bean = prepareBean("bean");
        TestBean beanWithAlias = prepareBean("beanWithAlias");
        given(applicationContext.getBeanDefinitionNames()).willReturn(new String[]{
            bean.name(),
            beanWithAlias.name()
        });
        given(applicationContext.getAliases(beanWithAlias.name())).willReturn(new String[] {"beanAlias" });

        // when
        Map<String, Object> shellContext = GroovyChange.createShellContext(applicationContext, out);

        // then
        assertFalse(shellContext.containsKey("beanWithAlias"));
        assertTrue(shellContext.containsKey("bean"));
        assertTrue(shellContext.containsKey("beanAlias"));
    }

    private TestBean prepareBean(String name) {
        return prepareBean(name, false);
    }

    private TestBean prepareBean(String name, boolean isAbstract) {
        TestBean bean = new TestBean(name);
        given(applicationContext.getBean(bean.name())).willReturn(bean);
        given(applicationContext.getAliases(bean.name())).willReturn(new String[0]);

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setAbstract(isAbstract);
        given(applicationContext.getBeanDefinition(bean.name())).willReturn(beanDefinition);

        return bean;
    }

    private static class TestBean {
        private final String name;

        public TestBean(String name) {
            this.name = name;
        }

        String name() {
            return name;
        }

    }

}
