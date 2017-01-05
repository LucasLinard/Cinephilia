package tech.linard.android.cinephilia.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by llinard on 04/01/17.
 */

public class Movie implements Parcelable {
    private int     id;
    private String  originalTitle;
    private String originalLanguage;
    private String localTitle;
    private String  overview;
    private String releaseDate;
    private int     genresCount;
    private int[]   genresIds;

    private String posterPath;
    private String backdropPath;

    private double  popularity;
    private double  voteAverage;
    private int     voteCount;

    private boolean video;
    private boolean adult;

    public Movie(int id,
                 String originalTitle,
                 String originalLanguage,
                 String localTitle,
                 String overview,
                 String releaseDate,
                 int genresCount,
                 int[] genresIds,
                 String posterPath,
                 String backdropPath,
                 double popularity,
                 double voteAverage,
                 int voteCount,
                 boolean video,
                 boolean adult) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.localTitle = localTitle;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genresCount = genresCount;
        this.genresIds = genresIds;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.video = video;
        this.adult = adult;
    }

    public Movie(Parcel source) {
        this.id = source.readInt();
        this.originalTitle = source.readString();
        this.originalLanguage = source.readString();
        this.localTitle = source.readString();
        this.overview = source.readString();
        this.releaseDate = source.readString();

        this.genresCount = source.readInt();
        int[] genresIntArray = new int[genresCount];
        source.readIntArray(genresIntArray);
        this.setGenresIds(genresIntArray);

        this.posterPath = source.readString();
        this.backdropPath = source.readString();

        this.popularity = source.readDouble();
        this.voteAverage = source.readDouble();
        this.voteCount = source.readInt();

        boolean[] myBooleanArray = new boolean[2];
        source.readBooleanArray(myBooleanArray);
        this.video = myBooleanArray[0];
        this.adult = myBooleanArray[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getOriginalTitle());
        dest.writeString(getOriginalLanguage());
        dest.writeString(getLocalTitle());
        dest.writeString(getOverview());
        dest.writeString(getReleaseDate());

        dest.writeInt(getGenresCount());
        dest.writeIntArray(getGenresIds());

        dest.writeString(getPosterPath());
        dest.writeString(getBackdropPath());

        dest.writeDouble(getPopularity());
        dest.writeDouble(getVoteAverage());
        dest.writeInt(getVoteCount());

        boolean[] booleanArray = new boolean[2];
        booleanArray [0] = video;
        booleanArray [1] = adult;
        dest.writeBooleanArray(booleanArray);

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

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
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

    public int[] getGenresIds() {
        return genresIds;
    }

    public void setGenresIds(int[] genresIds) {
        this.genresIds = genresIds;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
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

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public int getGenresCount() {
        return genresCount;
    }

    public void setGenresCount(int genresCount) {
        this.genresCount = genresCount;
    }
}