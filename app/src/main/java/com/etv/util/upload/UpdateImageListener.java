package com.etv.util.upload;


public interface UpdateImageListener {
    void updateImageFailed(String errorDesc);

    void updateImageProgress(int progress);

    void updateImageSuccess(String desc);


}
