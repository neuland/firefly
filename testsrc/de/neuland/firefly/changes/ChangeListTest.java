package de.neuland.firefly.changes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class ChangeListTest {
    private static final String ID = "ID";
    private static final String AUTHOR = "AUTHOR";
    private static final String FILE = "FILE";

    private ChangeList changeList;
    @Mock private Change change;

    @Before
    public void setUp() throws Exception {
        changeList = new ChangeList();
        given(change.getId()).willReturn(ID);
        given(change.getAuthor()).willReturn(AUTHOR);
        given(change.getFile()).willReturn(FILE);
    }

    @Test
    public void shouldContainChange() throws Exception {
        // given
        changeList.addChange(change);
        // when
        boolean result = changeList.contains(FILE, AUTHOR, ID);
        // then
        assertTrue(result);
    }

    @Test
    public void shouldNotContainChange() throws Exception {
        // when
        boolean result = changeList.contains(FILE, AUTHOR, ID);
        // then
        assertFalse(result);
    }

    @Test
    public void shouldNotAddSameChangeTwice() throws Exception {
        // when
        changeList.addChange(change);
        changeList.addChange(change);
        // then
        assertEquals(1, changeList.getChanges().size());
    }

    @Test
    public void shouldNotAddNullChange() throws Exception {
        // when
        changeList.addChange(null);
        // then
        assertEquals(0, changeList.getChanges().size());
    }
}
