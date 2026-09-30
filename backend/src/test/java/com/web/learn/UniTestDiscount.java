package com.web.learn;

import com.web.entity.UserEntity;
import com.web.util.Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UniTestDiscount {

    @Test
    void discountTenPercent(){
        BigDecimal price = new BigDecimal("100000");
        BigDecimal expected = new BigDecimal("90000");

        int percent = 10;

        BigDecimal actual = Utils.calsubPercent(price,percent);
        assertEquals(0,expected.compareTo(actual));
    }

    @ParameterizedTest
    @CsvSource({
            "10000,1,9900","50000,50,25000","70000,30,49000"
    })
    void discountExamples(String original,int percent, String expected){
        BigDecimal actual = Utils.calsubPercent(new BigDecimal(original),percent);
        assertEquals(0,new BigDecimal(expected).compareTo(actual));
    }

    @Test

    void userDiposit100kandWallet70k(){
        UserEntity user = new UserEntity();
        user.initDefaultUser();
        BigDecimal dispositValue = new BigDecimal("100000");
        BigDecimal walletValue = new BigDecimal("70000");
        BigDecimal expected = new BigDecimal("30000");

        user.deposit(dispositValue);
        user.wallet(walletValue);

        assertEquals(0,expected.compareTo(user.getCurrentBalance()));

    }

    @Test
    @DisplayName("Discount vượt quá 100%")
    void rejectDiscountOverOneHundred(){
        assertThrows(IllegalArgumentException.class,()->Utils.calsubPercent(new BigDecimal("100"),101));
    }

    @Test
    @DisplayName("Làm tròn discount đến số thấp phân thứ 2")
    void discountThirtyThreePercentOf19999(){
        BigDecimal price = new BigDecimal("19999");
        BigDecimal expected = new BigDecimal("13399.33");
        System.out.println((Utils.calsubPercent(price,33)));

        assertEquals(0,expected.compareTo(Utils.calsubPercent(price,33)));
    }


    @ParameterizedTest
    @CsvSource({
            "10000,-1,9900","50000,101,50500","70000,30,49000"
    })
    @DisplayName("Test discount và discount  = -1 và = 101")
    void testDiscountPercent(String original,int percent, String expected){
        if(percent < 0 || percent > 100){
            assertThrows(IllegalArgumentException.class,()->Utils.calsubPercent(new BigDecimal("original"),percent));
        }else{
            BigDecimal actual = Utils.calsubPercent(new BigDecimal(original),percent);

            assertEquals(0,new BigDecimal(expected).compareTo(actual));
        }
    }

    @Test
    @DisplayName("Test 2 lần giảm giá 10% với giá 100k")
    void testDoubleCalsubDoubleTenPercent(){
        BigDecimal price = new BigDecimal("100000");
        BigDecimal expected = new BigDecimal("80000");
        int percentDiscountProduct = 10;
        int percentDiscountCoupon = 10;
        int sumPercent = percentDiscountCoupon + percentDiscountProduct;

        assertEquals(0,expected.compareTo(Utils.calsubPercent(price,sumPercent)));




    }






}
