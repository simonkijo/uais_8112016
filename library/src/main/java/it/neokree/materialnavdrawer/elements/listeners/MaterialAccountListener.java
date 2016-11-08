package it.neokree.materialnavdrawer.elements.listeners;

import it.neokree.materialnavdrawer.elements.MaterialAccount;

/**
 * Created by neokree on 11/12/14.
 */
public interface MaterialAccountListener {

    void onAccountOpening(MaterialAccount account);

    void onChangeAccount(MaterialAccount newAccount);

}
