import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val Bookmark_add: ImageVector
	get() {
		if (_Bookmark_add != null) {
			return _Bookmark_add!!
		}
		_Bookmark_add = ImageVector.Builder(
            name = "Bookmark_add",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
			path(
    			fill = SolidColor(Color.Black),
    			fillAlpha = 1.0f,
    			stroke = null,
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 1.0f,
    			strokeLineCap = StrokeCap.Butt,
    			strokeLineJoin = StrokeJoin.Miter,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(200f, 840f)
				verticalLineToRelative(-640f)
				quadToRelative(0f, -33f, 23.5f, -56.5f)
				reflectiveQuadTo(280f, 120f)
				horizontalLineToRelative(240f)
				verticalLineToRelative(80f)
				horizontalLineTo(280f)
				verticalLineToRelative(518f)
				lineToRelative(200f, -86f)
				lineToRelative(200f, 86f)
				verticalLineToRelative(-278f)
				horizontalLineToRelative(80f)
				verticalLineToRelative(400f)
				lineTo(480f, 720f)
				close()
				moveToRelative(80f, -640f)
				horizontalLineToRelative(240f)
				close()
				moveToRelative(400f, 160f)
				verticalLineToRelative(-80f)
				horizontalLineToRelative(-80f)
				verticalLineToRelative(-80f)
				horizontalLineToRelative(80f)
				verticalLineToRelative(-80f)
				horizontalLineToRelative(80f)
				verticalLineToRelative(80f)
				horizontalLineToRelative(80f)
				verticalLineToRelative(80f)
				horizontalLineToRelative(-80f)
				verticalLineToRelative(80f)
				close()
			}
		}.build()
		return _Bookmark_add!!
	}

private var _Bookmark_add: ImageVector? = null
