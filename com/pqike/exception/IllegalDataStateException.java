/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pqike.exception;

/**
 *
 * @author User
 */
public class IllegalDataStateException extends Exception{
    public IllegalDataStateException(String errorLocation)
    {
        super("Illegal Data State at " + errorLocation);        
    }
}