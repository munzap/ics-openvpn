/*
 * Copyright (c) 2012-2014 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.user;

public interface AccountValidationInterface {

	public void accountValidated(ValidationResponse res);
	public void accountValidationFailed(ValidationAsyncResponse res);
}
