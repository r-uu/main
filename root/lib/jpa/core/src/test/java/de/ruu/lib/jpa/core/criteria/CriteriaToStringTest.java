package de.ruu.lib.jpa.core.criteria;

import de.ruu.lib.jpa.core.criteria.restriction.Restrictions;
import jakarta.persistence.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CriteriaToStringTest {

    @Entity
    static class Dummy { }

    @Test
    void eqProducesExpectedEqlAndParams() {
        Criteria<Dummy> c = Criteria.forClass(Dummy.class)
                .add(Restrictions.eq("name", "Bob"));
        assertEquals("select this from Dummy as this where this.name=? [[Bob]]", c.toString());
    }

    @Test
    void conjunctionCombinesClausesAndParams() {
        Criteria<Dummy> c = Criteria.forClass(Dummy.class)
                .add(Restrictions.<Dummy>conjunction()
                        .add(Restrictions.isNull("name"))
                        .add(Restrictions.ge("age", 18))
                );
        assertEquals("select this from Dummy as this where (this.name is null and this.age>=?) [[18]]", c.toString());
    }
}
