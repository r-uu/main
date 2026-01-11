package de.ruu.lib.fx.control.textfield.number;

import javafx.scene.control.TextField;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("TODO find out how to make these tests work")
@ExtendWith(ApplicationExtension.class)
class TestBigDecimalTextFormatter
{
    @Test void testInteger()
    {
        final Integer integer = 10;
        final TextField textField = new TextField();
        
        textField.setTextFormatter(BigDecimalTextFormatter.formatter());
        textField.setText(integer.toString());
        
        assertEquals(integer, Integer.valueOf(textField.getText()));
    }

    @Test void testIntegerNegative()
    {
        final Integer integer = -10;
        final TextField textField = new TextField();
        
        textField.setTextFormatter(BigDecimalTextFormatter.formatter());
        textField.setText(integer.toString());
        
        assertEquals(integer, Integer.valueOf(textField.getText()));
    }

    @Test void testBigDecimal()
    {
        final BigDecimal bigDecimal = BigDecimal.valueOf(10.0);
        final TextField textField = new TextField();
        
        textField.setTextFormatter(BigDecimalTextFormatter.formatter());
        textField.setText(bigDecimal.toString());
        
        assertEquals(0, bigDecimal.compareTo(new BigDecimal(textField.getText())));
    }

    @Test void testBigDecimalNegative()
    {
        final BigDecimal bigDecimal = BigDecimal.valueOf(-10.0);
        final TextField textField = new TextField();
        
        textField.setTextFormatter(BigDecimalTextFormatter.formatter());
        textField.setText(bigDecimal.toString());
        
        assertEquals(0, bigDecimal.compareTo(new BigDecimal(textField.getText())));
    }
}