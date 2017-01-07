package tech.linard.android.cinephilia.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by llinard on 04/01/17.
 */

public class Movie implements Parcelable {
    private int     id;
    private String originalTitle;
    private String localTitle;
    private String  overview;
    private String releaseDate;

    private String posterPath;

    private double  popularity;
    private double  voteAverage;
    private int     voteCount;

    public Movie(int id,
                 String originalTitle,
                 String localTitle,
                 String overview,
                 String releaseDate,
                 String posterPath,
                 double popularity,
                 double voteAverage,
                 int voteCount) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.localTitle = localTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    public Movie(Parcel source) {
        this.id = source.readInt();
        this.originalTitle = source.readString();
        this.localTitle = source.readString();
        this.overview = source.readString();
        this.releaseDate = source.readString();

        this.posterPath = source.readString();

        this.popularity = source.readDouble();
        this.voteAverage = source.readDouble();
        this.voteCount = source.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getOriginalTitle());
        dest.writeString(getLocalTitle());
        dest.writeString(getOverview());
        dest.writeString(getReleaseDate());
        dest.writeString(getPosterPath());
        dest.writeDouble(getPopularity());
        dest.writeDouble(getVoteAverage());
        dest.writeInt(getVoteCount());
        }
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);   
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getLocalTitle() {
        return localTitle;
    }

    public void setLocalTitle(String localTitle) {
        this.localTitle = localTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

}