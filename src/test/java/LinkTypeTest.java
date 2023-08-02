import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.sikderithub.keyboard.CommonMethod;
import com.sikderithub.keyboard.Utils.Common;

import org.junit.Test;

/**
 * Created by Maruf Ahmed on 01,August,2023
 * dev00.maruf@gmail.com
 */
public class LinkTypeTest {

    @Test
    public void linkPlatFormTest(){
        String fbLink ="http://ww.facebook.com/photo/?fbid=303482575552041&set=a.294573169776315";
        boolean isFacebookLink = Common.isFacebookLink(fbLink);
        assertTrue(isFacebookLink);

        String youtubeLink ="https://www.youtube.com/watch?v=fEnSDCmRnlY&ab_channel=PCBuilderBangladesh";
        boolean isYoutubeLink = Common.isYouTubeLink(youtubeLink);
        assertTrue(isYoutubeLink);

        String playLink ="https://play.google.com/store/apps/details?id=com.logicline.mydining";
        boolean isPlayLink = Common.isPlayStoreLink(playLink);
        assertTrue(isPlayLink);

        boolean isValidUrl = Common.isValidUrl(fbLink);
        assertTrue(isValidUrl);
    }
}
