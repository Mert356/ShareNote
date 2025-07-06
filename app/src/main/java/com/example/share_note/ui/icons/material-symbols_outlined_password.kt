import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.example.share_note.ui.theme.Green

public val Password: ImageVector
	get() {
		if (_Password != null) {
			return _Password!!
		}
		_Password = ImageVector.Builder(
            name = "Password",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
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
				moveTo(80f, 760f)
				verticalLineToRelative(-80f)
				horizontalLineToRelative(800f)
				verticalLineToRelative(80f)
				close()
				moveToRelative(46f, -242f)
				lineToRelative(-52f, -30f)
				lineToRelative(34f, -60f)
				horizontalLineTo(40f)
				verticalLineToRelative(-60f)
				horizontalLineToRelative(68f)
				lineToRelative(-34f, -58f)
				lineToRelative(52f, -30f)
				lineToRelative(34f, 58f)
				lineToRelative(34f, -58f)
				lineToRelative(52f, 30f)
				lineToRelative(-34f, 58f)
				horizontalLineToRelative(68f)
				verticalLineToRelative(60f)
				horizontalLineToRelative(-68f)
				lineToRelative(34f, 60f)
				lineToRelative(-52f, 30f)
				lineToRelative(-34f, -60f)
				close()
				moveToRelative(320f, 0f)
				lineToRelative(-52f, -30f)
				lineToRelative(34f, -60f)
				horizontalLineToRelative(-68f)
				verticalLineToRelative(-60f)
				horizontalLineToRelative(68f)
				lineToRelative(-34f, -58f)
				lineToRelative(52f, -30f)
				lineToRelative(34f, 58f)
				lineToRelative(34f, -58f)
				lineToRelative(52f, 30f)
				lineToRelative(-34f, 58f)
				horizontalLineToRelative(68f)
				verticalLineToRelative(60f)
				horizontalLineToRelative(-68f)
				lineToRelative(34f, 60f)
				lineToRelative(-52f, 30f)
				lineToRelative(-34f, -60f)
				close()
				moveToRelative(320f, 0f)
				lineToRelative(-52f, -30f)
				lineToRelative(34f, -60f)
				horizontalLineToRelative(-68f)
				verticalLineToRelative(-60f)
				horizontalLineToRelative(68f)
				lineToRelative(-34f, -58f)
				lineToRelative(52f, -30f)
				lineToRelative(34f, 58f)
				lineToRelative(34f, -58f)
				lineToRelative(52f, 30f)
				lineToRelative(-34f, 58f)
				horizontalLineToRelative(68f)
				verticalLineToRelative(60f)
				horizontalLineToRelative(-68f)
				lineToRelative(34f, 60f)
				lineToRelative(-52f, 30f)
				lineToRelative(-34f, -60f)
				close()
			}
		}.build()
		return _Password!!
	}

private var _Password: ImageVector? = null
