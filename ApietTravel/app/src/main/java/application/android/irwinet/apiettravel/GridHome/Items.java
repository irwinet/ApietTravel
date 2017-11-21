package application.android.irwinet.apiettravel.GridHome;

/**
 * Created by Irwinet on 20/11/2017.
 */

public class Items {
    String homeListName;
    int homeListImage;

    public Items(String homeListName, int homeListImage) {
        this.homeListName = homeListName;
        this.homeListImage = homeListImage;
    }

    public String getHomeListName() {
        return homeListName;
    }

    public void setHomeListName(String homeListName) {
        this.homeListName = homeListName;
    }

    public int getHomeListImage() {
        return homeListImage;
    }

    public void setHomeListImage(int homeListImage) {
        this.homeListImage = homeListImage;
    }
}
