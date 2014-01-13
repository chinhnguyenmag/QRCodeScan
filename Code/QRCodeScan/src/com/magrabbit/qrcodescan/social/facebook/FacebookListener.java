package com.magrabbit.qrcodescan.social.facebook;

import com.magrabbit.qrcodescan.model.FaceBookAccount;

public interface FacebookListener {
	void facebookLoginSuccess(FaceBookAccount facebookAccount);

	void facebookLoginError();

	void facebookLoginFail();
}
