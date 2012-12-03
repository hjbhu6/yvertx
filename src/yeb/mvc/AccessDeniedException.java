/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yeb.mvc;

/**
 *
 * @author Christian
 */
public class AccessDeniedException extends Error{
    public AccessDeniedException(String msg) {
        super(msg);
    }
}
