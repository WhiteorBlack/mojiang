package cn.idcby.jiajubang.Bean;

/**
 *
 */

public class NomalRvCategory{
    private boolean isSelected = false ;

    private String categoryTitle ;

    public NomalRvCategory() {
    }

    public NomalRvCategory(boolean isSelected ,String title){
        this.isSelected = isSelected ;
        this.categoryTitle = title ;
    }

    public NomalRvCategory(QuestionCategory category){
        this.isSelected = category.isSelected() ;
        this.categoryTitle = category.getCategoryTitle() ;
    }
    public NomalRvCategory(NewsCategory category){
        this.categoryTitle = category.CategoryTitle ;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public boolean isSelected() {
            return isSelected;
    }

    public void setSelected(boolean selected) {
                isSelected = selected;
        }
}
